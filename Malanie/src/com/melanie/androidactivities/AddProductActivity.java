package com.melanie.androidactivities;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.epson.lwprint.sdk.LWPrint;
import com.epson.lwprint.sdk.LWPrintCallback;
import com.epson.lwprint.sdk.LWPrintDiscoverConnectionType;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinter;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinterCallback;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.epson.lwprint.sdk.LWPrintPrintingPhase;
import com.melanie.androidactivities.support.MelanieBarcodeDataProvider;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class AddProductActivity extends Activity {

	private ProductEntryController productController;

	private final int BLUETOOTH_REQUEST_CODE = 28;
	private final int CUT_EACH_TAPE = 0;
	private final int DENSITY = -2;

	private Map<String, String> printerInfo = null;
	private LWPrint printer;
	private LWPrintDiscoverPrinter printerDiscoverHelper = null;

	private BluetoothAdapter bluetoothAdapter = null;
	private boolean wasTurnedOnByMelanie;

	private String currentBarcode = null;
	private int currentProductQuantity = 1;
	private boolean isPrinterFound = false;

	private ProgressDialog progressDialog = null;
	private Handler handler;
	private ScheduledExecutorService executorService;

	private int printPhaseMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);

		initializeFields();

		List<Category> categories = getAllCategories();
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		categorySpinner.setAdapter(new ArrayAdapter<Category>(this,
				android.R.layout.simple_spinner_dropdown_item, categories));

		initializePrinter();
	}

	private void initializeFields() {
		productController = MelanieBusinessFactory.makeProductEntryController();
		handler = new Handler();
		createPrintProgressDialog();
	}

	private List<Category> getAllCategories() {
		List<Category> categories = null;
		try {
			categories = productController.getAllCategories();
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // log it
		}
		return categories;
	}

	private void createPrintProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage(getText(R.string.printPreparing));
			progressDialog.setIcon(R.drawable.printer);
			progressDialog.setMax(100);
			setProgressBarHandlers();
		}
	}

	private void setProgressBarHandlers() {
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				getText(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressDialog.setProgress(0);
						progressDialog.cancel();

					}
				});
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						printer.cancelPrint();
					}
				});
	}

	/**
	 * Add a new product from the user input
	 * 
	 * @param view
	 *            the add button
	 */
	public void addProduct(View view) {

		Category category = getSelectedCategory();
		String productName = ((EditText) findViewById(R.id.productName))
				.getText().toString();
		String priceStr = ((EditText) findViewById(R.id.price)).getText()
				.toString();
		double price = Double.parseDouble(priceStr);

		OperationResult result = addProductAndReturnResult(category,
				productName, price, currentProductQuantity);
		if (result == OperationResult.SUCCESSFUL) {
			printBarcode();
			clearTextFields();
		}
		Utils.makeToastBasedOnOperationResult(this, result,
				R.string.productAddSuccessful, R.string.productAddFailed);
	}

	private OperationResult addProductAndReturnResult(Category category,
			String productName, double price, int quantity) {
		OperationResult result = OperationResult.FAILED;
		if (category != null) {
			try {
				int lastProductId = productController
						.getLastInsertedProductId();
				currentBarcode = generateBarcodeString(lastProductId,
						category.getId());
				result = productController
						.addProduct(productName, currentProductQuantity, price,
								category, currentBarcode);
			} catch (MelanieBusinessException e) {
				e.printStackTrace(); // log it
			}
		}
		return result;
	}

	private String generateBarcodeString(int lastItemId, int categoryId) {
		lastItemId++;
		int trailingZeroes = 12 - String.valueOf(categoryId).length();
		String format = "%0" + trailingZeroes + "d";
		String barcodeNumber = categoryId + String.format(format, lastItemId);
		return barcodeNumber;
	}
	
	private void clearTextFields() {
		Utils.clearInputTextFields(findViewById(R.id.productName),
				findViewById(R.id.quantity), findViewById(R.id.price));
	}

	private Category getSelectedCategory() {
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		return (Category) categorySpinner.getSelectedItem();
	}

	private void initializePrinter() {
		if (canConnectBluetooth() && printer == null && printerInfo == null) {
			discoverLWPrinter();
			printer = new LWPrint(this);
			printer.setCallback(new PrintCallBack());
		}
	}

	private void printBarcode() {
		if (isPrinterFound)
			performPrint();
		else
			Utils.makeToast(this, R.string.printerNotFound);
	}

	private boolean canConnectBluetooth() {

		boolean canConnect = false;

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Utils.makeToast(this, R.string.bluetoothNotSupported);
		} else {
			enableBluetooth();
			canConnect = true;
		}
		return canConnect;
	}

	private void enableBluetooth() {
		Intent enableBluetoothIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableBluetoothIntent, BLUETOOTH_REQUEST_CODE);
	}

	private void disableBluetooth() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
			bluetoothAdapter.disable();
	}

	private void discoverLWPrinter() {

		EnumSet<LWPrintDiscoverConnectionType> flag = EnumSet
				.of(LWPrintDiscoverConnectionType.ConnectionTypeBluetooth);
		printerDiscoverHelper = new LWPrintDiscoverPrinter(null, null, flag);
		printerDiscoverHelper.setCallback(new DiscoverPrinterCallback());
		printerDiscoverHelper.startDiscover(this);
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
				if (progressDialog != null)
					progressDialog.show();
			}
		}, 2);

		new MelaniePrintAsyncTask().execute(printer, printerInfo,
				getPrintSettings(), getAssets(), currentBarcode);

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

						progressDialog.setProgress((int) (printer
								.getProgressOfPrint() * 100));
						progressDialog.setMessage(getText(printPhaseMessage));
					}

				});
			}
		}, 2, 2, TimeUnit.SECONDS);
	}

	private class PrintCallBack implements LWPrintCallback {

		private SparseIntArray phaseMessage;

		public PrintCallBack() {
			phaseMessage = new SparseIntArray(3) {
				{
					put(LWPrintPrintingPhase.Prepare, R.string.printPreparing);
					put(LWPrintPrintingPhase.Processing, R.string.printing);
					put(LWPrintPrintingPhase.Complete,
							R.string.printingComplete);
					put(LWPrintPrintingPhase.WaitingForPrint,
							R.string.waitingForPrint);
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
			if (phase == LWPrintPrintingPhase.Complete)
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						progressDialog.dismiss();
						progressDialog.setProgress(0);
					}
				}, 1200);
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

	private class DiscoverPrinterCallback implements
			LWPrintDiscoverPrinterCallback {

		@Override
		public void onFindPrinter(LWPrintDiscoverPrinter printerDiscoverHelper,
				Map<String, String> foundPrinterInfo) {

			AddProductActivity a = AddProductActivity.this;
			a.isPrinterFound = true;
			a.printerInfo = foundPrinterInfo;
			a.wasTurnedOnByMelanie = true;
			if (a.printerDiscoverHelper != null)
				a.printerDiscoverHelper.stopDiscover();
		}

		@Override
		public void onRemovePrinter(LWPrintDiscoverPrinter arg0,
				Map<String, String> arg1) {
			// Do nothing
		}

	}

	private class MelaniePrintAsyncTask extends AsyncTask<Object, Void, Void> {

		private static final int PRINTER = 0;
		private static final int PRINTER_INFO = 1;
		private static final int PRINT_SETTINGS = 2;
		private static final int ASSET_MANAGER = 3;
		private static final int BARCODE = 4;

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Object... params) {
			LWPrint printer = (LWPrint) params[PRINTER];
			Map<String, String> printerInfo = (Map<String, String>) params[PRINTER_INFO];
			HashMap<String, Object> printSettings = (HashMap<String, Object>) params[PRINT_SETTINGS];
			AssetManager assetManager = (AssetManager) params[ASSET_MANAGER];
			String barcode = (String) params[BARCODE];

			printer.setPrinterInformation(printerInfo);
			printer.doPrint(new MelanieBarcodeDataProvider(assetManager,
					barcode), printSettings);
			return null;
		}

	}

	@Override
	public void onPause() {
		super.onDestroy();
		if (executorService != null)
			executorService.shutdown();
		if (wasTurnedOnByMelanie)
			disableBluetooth();
	}
}
