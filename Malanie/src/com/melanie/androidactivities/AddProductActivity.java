package com.melanie.androidactivities;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.epson.lwprint.sdk.LWPrint;
import com.epson.lwprint.sdk.LWPrintCallback;
import com.epson.lwprint.sdk.LWPrintDiscoverConnectionType;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinter;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinterCallback;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.epson.lwprint.sdk.LWPrintPrintingPhase;
import com.melanie.androidactivities.support.MelanieBarcodeDataProvider;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.controllers.ProductEntryController;
import com.melanie.business.controllers.ProductEntryControllerImpl;
import com.melanie.entities.ProductCategory;
import com.melanie.support.exceptions.MelanieArgumentException;

public class AddProductActivity extends Activity {

	private ProductEntryController productController;
	private final int BLUETOOTH_REQUEST_CODE = 28;
	private final int CUT_EACH_TAPE = 0;
	private final int DENSITY = 0;
	private Map<String, String> printerInfo = null;
	private LWPrint printer;
	private String currentBarcode = null;
	private int currentProductQuantity = 1;
	private boolean isPrinterFound = false;

	public AddProductActivity() {
		productController = new ProductEntryControllerImpl();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);

		List<ProductCategory> categories = productController.getAllCategories();

		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		categorySpinner.setAdapter(new ArrayAdapter<ProductCategory>(this,
				android.R.layout.simple_spinner_dropdown_item, categories));

		initializePrinter();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_product, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addProduct(View view) {

		ProductCategory productCategory = getSelectedCategory();
		String productName = ((EditText) findViewById(R.id.productName))
				.getText().toString();
		String priceStr = ((EditText) findViewById(R.id.price)).getText()
				.toString();
		double price = Double.parseDouble(priceStr);

		if (productCategory != null) {
			int lastProductId = productController.getLastInsertedProductId();
			currentBarcode = Utils.generateBarcodeString(lastProductId,
					productCategory.getId());
			try {
				productController.addProduct(productName,
						currentProductQuantity, price, productCategory);
			} catch (MelanieArgumentException e) {
				e.printStackTrace(); // Use logger
			}
			printBarcode();
		}
	}

	private ProductCategory getSelectedCategory() {
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		return (ProductCategory) categorySpinner.getSelectedItem();
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
			Toast.makeText(this, R.string.printerNotFound, Toast.LENGTH_LONG)
					.show();
	}

	private boolean canConnectBluetooth() {

		boolean canConnect = false;

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(),
					R.string.bluetoothNotSupported, Toast.LENGTH_SHORT).show();
			canConnect = false;
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

	private void discoverLWPrinter() {

		EnumSet<LWPrintDiscoverConnectionType> flag = EnumSet
				.of(LWPrintDiscoverConnectionType.ConnectionTypeBluetooth);
		LWPrintDiscoverPrinter printerDiscoverHelper = new LWPrintDiscoverPrinter(
				null, null, flag);
		printerDiscoverHelper.setCallback(new DiscoverPrinterCallback());
		printerDiscoverHelper.startDiscover(this);

	}

	private HashMap<String, Object> getPrintSettings(int productQuantity) {

		HashMap<String, Object> printSettings = new HashMap<String, Object>();
		printSettings.put(LWPrintParameterKey.Copies, productQuantity);
		printSettings.put(LWPrintParameterKey.HalfCut, true);
		printSettings.put(LWPrintParameterKey.TapeCut, CUT_EACH_TAPE);
		printSettings.put(LWPrintParameterKey.PrintSpeed, true);
		printSettings.put(LWPrintParameterKey.Density, DENSITY);

		return printSettings;
	}

	private void performPrint() {

		new MelaniePrintAsyncTask().execute(printer, printerInfo,
				getPrintSettings(currentProductQuantity), getAssets(),
				currentBarcode);
	}

	private class PrintCallBack implements LWPrintCallback {

		@Override
		public void onAbortPrintOperation(LWPrint arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAbortTapeFeedOperation(LWPrint arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChangePrintOperationPhase(LWPrint print, int phase) {
			switch (phase) {
			case LWPrintPrintingPhase.Prepare:
				System.out.println("AKWASI OWUSU :  Preparing");
				break;
			case LWPrintPrintingPhase.Processing:
				System.out.println("AKWASI OWUSU: Processing");
				break;
			case LWPrintPrintingPhase.WaitingForPrint:
				System.out.println("AKWASI OWUSU: Waiting for print");
				break;
			case LWPrintPrintingPhase.Complete:
				System.out.println("Printing complete");

			default:
				break;
			}
			if (phase == LWPrintPrintingPhase.Complete)
				Log.i("AKWASI OWUSU:::", "Printing Complete");
		}

		@Override
		public void onChangeTapeFeedOperationPhase(LWPrint arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSuspendPrintOperation(LWPrint arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

	}

	private class DiscoverPrinterCallback implements
			LWPrintDiscoverPrinterCallback {

		@Override
		public void onFindPrinter(LWPrintDiscoverPrinter printerDiscoverHelper,
				Map<String, String> printerInfo) {

			AddProductActivity.this.isPrinterFound = true;
			AddProductActivity.this.printerInfo = printerInfo;

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
}
