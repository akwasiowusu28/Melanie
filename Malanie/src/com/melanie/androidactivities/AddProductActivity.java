package com.melanie.androidactivities;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.epson.lwprint.sdk.LWPrintDiscoverConnectionType;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinter;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinterCallback;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.controllers.ProductEntryController;
import com.melanie.business.controllers.ProductEntryControllerImpl;
import com.melanie.entities.Category;

public class AddProductActivity extends Activity {

	private ProductEntryController productController;
	private final int BLUETOOTH_REQUEST_CODE = 28;
	private final int CUT_EACH_TAPE = 0;
	private final int DENSITY = 0;
	
	public AddProductActivity() {
		productController = new ProductEntryControllerImpl();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);
		
		List<Category> categories = productController.getAllCategories();
		
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		categorySpinner.setAdapter(new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item ,categories));
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
	
	public void addProduct(View view){
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		Category selectedCategory = (Category)categorySpinner.getSelectedItem();
		int lastProductId = productController.getLastInsertedProductId();
		String barcode = Utils.generateBarcodeString(lastProductId, selectedCategory.getId());
	}
	
	private boolean canConnectBluetooth(){
		
	  boolean canConnect = false;
		
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter == null)
		{
			Toast.makeText(getApplicationContext(), R.string.bluetoothNotSupported, 2).show();
			canConnect = false;
		}
		else
		{
			Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetoothIntent, BLUETOOTH_REQUEST_CODE);
			canConnect = true;
		}
		return canConnect;
	}
	
	private void discoverLWPrinter(){
		new Thread(){
			
			public void run(){
				EnumSet<LWPrintDiscoverConnectionType> flag = EnumSet.of(LWPrintDiscoverConnectionType.ConnectionTypeBluetooth);
				LWPrintDiscoverPrinter printerDiscoverHelper = new LWPrintDiscoverPrinter(null, null, flag);
				printerDiscoverHelper.setCallback(new DiscoverPrinterCallback());
				printerDiscoverHelper.startDiscover(AddProductActivity.this);
			}
			
		}.start();
	}
	
	private HashMap<String, Object> getPrintSettings(int productQuantity){
		
		HashMap<String, Object> printSettings = new HashMap<String, Object>();
		printSettings.put(LWPrintParameterKey.Copies, productQuantity);
		printSettings.put(LWPrintParameterKey.HalfCut, true);
		printSettings.put(LWPrintParameterKey.TapeCut, CUT_EACH_TAPE);
		printSettings.put(LWPrintParameterKey.PrintSpeed, false);
		printSettings.put(LWPrintParameterKey.Density, DENSITY);
		
		return printSettings;
	}
	
	private static class DeviceInfo{
		public static String deviceName;
		public static String macAddress;
		public static String deviceStatus;
		public static boolean deviceDiscovered;
	}
	
	private class DiscoverPrinterCallback implements LWPrintDiscoverPrinterCallback{

		@Override
		public void onFindPrinter(LWPrintDiscoverPrinter printerDiscoverHelper,
				Map<String, String> printer) {

			if(!DeviceInfo.deviceDiscovered)
			{
				DeviceInfo.deviceName = printer.get(LWPrintDiscoverPrinter.PRINTER_INFO_NAME);
				DeviceInfo.macAddress = printer.get(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER);
				DeviceInfo.deviceStatus = printer.get(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS);
				DeviceInfo.deviceDiscovered = true;
			}
		}

		@Override
		public void onRemovePrinter(LWPrintDiscoverPrinter arg0,
				Map<String, String> arg1) {
			// Do nothing
		}
		
	}
}
