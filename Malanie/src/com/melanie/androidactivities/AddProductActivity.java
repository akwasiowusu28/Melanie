package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.epson.lwprint.sdk.LWPrint;
import com.epson.lwprint.sdk.LWPrintCallback;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.epson.lwprint.sdk.LWPrintPrintingPhase;
import com.melanie.androidactivities.support.MelanieBarcodeDataProvider;
import com.melanie.androidactivities.support.MelaniePrinterDiscoverer;
import com.melanie.androidactivities.support.PrinterType;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.support.BusinessFactory;
import com.melanie.support.CodeStrings;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

@SuppressWarnings("unchecked")
public class AddProductActivity extends AppCompatActivity {

	private ProductEntryController productController;

	private static final int CUT_EACH_TAPE = 0;
	private static final int DENSITY = -2;
	private static final int PRINTER_SELECT_REQUEST_CODE = 28;

	private Map<String, String> printerInfo = null;
	private LWPrint printer;

	private String currentBarcode = null;
	private int currentProductQuantity = 1;
	private boolean isPrinterFound = false;

	private boolean bluetoothEnableRefused = false;

	private static boolean bluetoothRequestMade;

	private ProgressDialog progressDialog = null;
	private Handler handler;
	private ScheduledExecutorService executorService;

	private int printPhaseMessage;
	private ArrayAdapter<Category> categoriesAdapter;
	private ArrayList<Category> categories;
	private MelaniePrinterDiscoverer printerDiscoverer;

	private boolean instanceWasSaved = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);

		if (savedInstanceState != null) {
			restoreInstanceState(savedInstanceState);
			instanceWasSaved = true;
		}

		initializeFields();

		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		categorySpinner.setAdapter(categoriesAdapter);


		if(!bluetoothRequestMade) {
			initializePrinter();
			bluetoothRequestMade = true;
		}
		setupAddProductListener();

	}

	private void initializeFields() {
		productController = BusinessFactory.makeProductEntryController();
		handler = new Handler(getMainLooper());
		if(!instanceWasSaved) {
			getAllCategories();
		}
		categoriesAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, categories);
		createPrintProgressDialog();
	}

	private void getAllCategories() {
		categories = new ArrayList<Category>();
		try {
			List<Category> tempCategories = null;
			tempCategories = productController.getAllCategories(new OperationCallBack<Category>() {
				@Override
				public void onCollectionOperationSuccessful(List<Category> results) {
					Utils.mergeItems(results, categories, false);
					Utils.notifyListUpdate(categoriesAdapter, handler);
				}
			});
			if (tempCategories != null && !tempCategories.isEmpty()) {
				categories.addAll(tempCategories);
			}

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO: log it
		}
	}

	private void createPrintProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage(getText(R.string.printPreparing));
			progressDialog.setMax(100);
			setProgressBarHandlers();
		}
	}

	private void setProgressBarHandlers() {
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.setProgress(0);
				progressDialog.cancel();

			}
		});
		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				printer.cancelPrint();
			}
		});
	}

	private void setupAddProductListener() {
		Button addProductButton = (Button) findViewById(R.id.addProductButton);
		addProductButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addProduct();
			}
		});
	}

	/**
	 * Add a new product from the user input
	 * 
	 * @param view
	 *            the add button
	 */
	public void addProduct() {

		Category category = getSelectedCategory();

		EditText productNameField = (EditText) findViewById(R.id.productName);
		EditText priceField = (EditText) findViewById(R.id.price);
		EditText quantityField = (EditText) findViewById(R.id.quantity);

		if(!isAnyFieldInvalid(priceField,productNameField, quantityField)){

			String productName = productNameField.getText().toString();
			String priceStr = priceField.getText().toString();
			String quantity = quantityField.getText().toString();

			double price = Double.parseDouble(priceStr);
			currentProductQuantity = Integer.parseInt(quantity);
			addProduct(category, productName, price);
		}
	}

	private boolean isAnyFieldInvalid(EditText... fields){
		boolean isAnyInValid = false;
		for(EditText field: fields)
		{
			if(field != null && field.getText().toString().equals(CodeStrings.EMPTY_STRING)) {
				Utils.switchInvalidFieldsBackColor(false, field);
				if(!isAnyInValid) {
					isAnyInValid = true;
				}
			} else {
				Utils.switchInvalidFieldsBackColor(true, field);
			}
		}
		return isAnyInValid;
	}

	private void addProduct(final Category category,
			final String productName, final double price) {

		try {
			productController.getLastInsertedProductId(new OperationCallBack<Integer>(){

				@Override
				public void onOperationSuccessful(Integer result) {

					performAddProduct(category, productName, price, result);
				}
			});
		} catch (MelanieBusinessException e) {
			Utils.makeToast(this, R.string.productAddFailed);
			// TODO Log it
			e.printStackTrace();
		}
	}

	private void performAddProduct( Category category,
			String productName,  double price, int lastProductId){

		currentBarcode = generateBarcodeString(lastProductId);
		try {
			productController.addProduct(productName, currentProductQuantity, price, category,
					currentBarcode);
			printBarcode();
			Utils.makeToast(AddProductActivity.this, R.string.productAddSuccessful);
			clearTextFields();
		} catch (MelanieBusinessException e) {
			Utils.makeToast(AddProductActivity.this, R.string.productAddFailed);
			// TODO log it
			e.printStackTrace();
		}
	}

	private String generateBarcodeString(int lastItemId) {
		lastItemId++;
		String format = "%06d";
		String barcodeNumber = Utils.getBarcodePrefix() + String.format(format, lastItemId);
		return barcodeNumber;
	}

	private void clearTextFields() {
		Utils.clearInputTextFields(findViewById(R.id.productName), findViewById(R.id.quantity),
				findViewById(R.id.price));
	}

	private Category getSelectedCategory() {
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		return (Category) categorySpinner.getSelectedItem();
	}

	private void initializePrinter() {
		if (!bluetoothEnableRefused && printer == null && printerInfo == null) {
			printerDiscoverer = new MelaniePrinterDiscoverer(this, new OperationCallBack<Map<String, String>>() {

				@Override
				public void onOperationSuccessful(Map<String, String> result) {
					printerInfo = result;
					isPrinterFound = true;
				}

			}, PrinterType.Barcode);

			printerDiscoverer.discoverBarcodePrinter();
		}
		printer = new LWPrint(this);
		printer.setCallback(new PrintCallBack());
	}

	private void printBarcode() {

		if (userWantsToPrintBarcode()) {
			if (isPrinterFound) {
				performPrint();
			} else {
				Intent intent = new Intent(this, SelectPrinterActivity.class);
				intent.putExtra(CodeStrings.PRINTER_TYPE, PrinterType.Barcode.toString());
				startActivityForResult(intent, PRINTER_SELECT_REQUEST_CODE);
			}
		}
	}

	private boolean userWantsToPrintBarcode() {
		CheckBox checkBox = (CheckBox) findViewById(R.id.printBarcodeCheckBox);
		return checkBox.isChecked();
	}

	private HashMap<String, Object> getPrintSettings() {

		HashMap<String, Object> printSettings = new HashMap<String, Object>();
		printSettings.put(LWPrintParameterKey.Copies, 1);
		printSettings.put(LWPrintParameterKey.HalfCut, false);
		printSettings.put(LWPrintParameterKey.TapeCut, CUT_EACH_TAPE);
		printSettings.put(LWPrintParameterKey.PrintSpeed, false);
		printSettings.put(LWPrintParameterKey.Density, DENSITY);

		return printSettings;
	}

	private void performPrint() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (progressDialog != null) {
					progressDialog.show();
				}
			}
		}, 2);

		new MelaniePrintAsyncTask().execute(printer, printerInfo, getPrintSettings(), getAssets(), currentBarcode);

		setProgressDialogUpdates();
	}

	private void setProgressDialogUpdates() {
		executorService = Executors.newScheduledThreadPool(2);
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {

						progressDialog.setProgress((int) (printer.getProgressOfPrint() * 100));
						progressDialog.setMessage(getText(printPhaseMessage));
					}

				});
			}
		}, 2, 2, TimeUnit.SECONDS);
	}

	private class PrintCallBack implements LWPrintCallback {

		private final SparseIntArray phaseMessage;

		public PrintCallBack() {
			phaseMessage = new SparseIntArray(3) {
				{
					put(LWPrintPrintingPhase.Prepare, R.string.printPreparing);
					put(LWPrintPrintingPhase.Processing, R.string.printing);
					put(LWPrintPrintingPhase.Complete, R.string.printingComplete);
					put(LWPrintPrintingPhase.WaitingForPrint, R.string.waitingForPrint);
				}
			};
		}

		@Override
		public void onAbortPrintOperation(LWPrint arg0, int arg1, int arg2) {
			// Do nothing for now

		}

		@Override
		public void onAbortTapeFeedOperation(LWPrint arg0, int arg1, int arg2) {
			// Do nothing for now

		}

		@Override
		public void onChangePrintOperationPhase(LWPrint print, int phase) {
			final int p = phase;
			handler.post(new Runnable() {
				@Override
				public void run() {
					printPhaseMessage = phaseMessage.get(p);
				}
			});
			if (phase == LWPrintPrintingPhase.Complete) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						progressDialog.setProgress(0);
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					}
				}, 1200);
			}
		}

		@Override
		public void onChangeTapeFeedOperationPhase(LWPrint arg0, int arg1) {
			// Do nothing for now

		}

		@Override
		public void onSuspendPrintOperation(LWPrint arg0, int arg1, int arg2) {
			// Do nothing for now

		}

	}

	private class MelaniePrintAsyncTask extends AsyncTask<Object, Void, Void> {

		private static final int PRINTER = 0;
		private static final int PRINTER_INFO = 1;
		private static final int PRINT_SETTINGS = 2;
		private static final int ASSET_MANAGER = 3;
		private static final int BARCODE = 4;

		@Override
		protected Void doInBackground(Object... params) {
			LWPrint printer = (LWPrint) params[PRINTER];
			Map<String, String> printerInfo = (Map<String, String>) params[PRINTER_INFO];
			HashMap<String, Object> printSettings = (HashMap<String, Object>) params[PRINT_SETTINGS];
			AssetManager assetManager = (AssetManager) params[ASSET_MANAGER];
			String barcode = (String) params[BARCODE];

			printer.setPrinterInformation(printerInfo);
			printer.doPrint(new MelanieBarcodeDataProvider(assetManager, barcode, currentProductQuantity),
					printSettings);
			return null;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
		if (resultCode == RESULT_OK && requestCode == PRINTER_SELECT_REQUEST_CODE) {

			Bundle bundle = intentData.getExtras();
			printerInfo = (Map<String, String>) bundle.get(CodeStrings.PRINTER_INFO);
			if (printerInfo != null) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
				performPrint();
			}
		} else if (requestCode == Utils.Constants.BLUETOOTH_REQUEST_CODE) {
			bluetoothEnableRefused = resultCode == RESULT_CANCELED;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {

		bundle.putSerializable(CodeStrings.CATEGORIES, categories);
		bundle.putBoolean(CodeStrings.BLUETOOTH_REFUSED, bluetoothEnableRefused);

		super.onSaveInstanceState(bundle);
	}

	private void restoreInstanceState(Bundle bundle){
		if(bundle != null){
			bluetoothEnableRefused = bundle.getBoolean(CodeStrings.BLUETOOTH_REFUSED);
			categories = (ArrayList<Category>) bundle.get(CodeStrings.CATEGORIES);
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (executorService != null) {
			executorService.shutdown();
			executorService = null;
		}

		if (printerDiscoverer != null) {
			printerDiscoverer.clearResources();
			printerDiscoverer = null;
		}

		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

}
