package com.melanie.androidactivities.support;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.epson.lwprint.sdk.LWPrintDiscoverConnectionType;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinter;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinterCallback;
import com.melanie.androidactivities.R;
import com.melanie.support.OperationCallBack;
import com.zj.btsdk.BluetoothService;

public class MelaniePrinterDiscoverer {

	private final OperationCallBack<Map<String, String>> operationCallBack;
	private LWPrintDiscoverPrinter printerDiscoverHelper;
	private BluetoothAdapter bluetoothAdapter = null;
	private BluetoothService receiptPrinterService;
	private final PrinterType printerType;
	private final Context context;
	private final boolean bluetoothRefused = false;

	public MelaniePrinterDiscoverer(Context context, OperationCallBack<Map<String, String>> operationCallBack,
			PrinterType printerType) {

		this.operationCallBack = operationCallBack;
		this.context = context;
		this.printerType = printerType;
		setupBluetooth();
	}

	private void setupBluetooth() {
		if (canConnectBluetooth()) {
			enableBluetooth();
			context.registerReceiver(bluetoothBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		}
	}

	boolean canConnectBluetooth() {
		boolean canConnect = true;

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			Utils.makeToast(context, R.string.bluetoothNotSupported);
			canConnect = false;
		}
		return canConnect;
	}

	private void enableBluetooth() {
		Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity) context).startActivityForResult(enableBluetoothIntent, Utils.Constants.BLUETOOTH_REQUEST_CODE);
	}

	public void discoverBarcodePrinter() {
		if (!bluetoothRefused) {
			EnumSet<LWPrintDiscoverConnectionType> flag = EnumSet
					.of(LWPrintDiscoverConnectionType.ConnectionTypeBluetooth);
			printerDiscoverHelper = new LWPrintDiscoverPrinter(null, null, flag);
			printerDiscoverHelper.setCallback(discoverBarcodePrinterCallBack);
			printerDiscoverHelper.startDiscover(context);
		}
	}

	private final LWPrintDiscoverPrinterCallback discoverBarcodePrinterCallBack = new LWPrintDiscoverPrinterCallback() {
		@Override
		public void onFindPrinter(LWPrintDiscoverPrinter arg0, Map<String, String> printerInfo) {
			if (printerDiscoverHelper != null) {
				printerDiscoverHelper.stopDiscover();
			}

			operationCallBack.onOperationSuccessful(printerInfo);
		}

		@Override
		public void onRemovePrinter(LWPrintDiscoverPrinter arg0, Map<String, String> arg1) {
		}

	};

	private final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			switch (action) {
			case BluetoothDevice.ACTION_FOUND:
				if (printerType.equals(PrinterType.Receipt)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String deviceName = device.getName();
					if (deviceName.contains("Printer")) {
						Map<String, String> deviceInfo = new HashMap<String, String>();
						deviceInfo.put(device.getName(), device.getAddress());
						operationCallBack.onOperationSuccessful(deviceInfo);
						receiptPrinterService.cancelDiscovery();
						context.unregisterReceiver(bluetoothBroadcastReceiver);
					}
					break;
				}
			}
		}
	};

	public boolean isBluetoothAvailable() {
		return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
	}

	public void discoverReceiptPrinter() {
		if (!bluetoothRefused) {
			receiptPrinterService = new BluetoothService(context, new Handler());
			receiptPrinterService.startDiscovery();
		}
	}

	public void clearResources() {
		if (printerType.equals(PrinterType.Receipt) && receiptPrinterService != null) {
			receiptPrinterService.cancelDiscovery();

			receiptPrinterService.stop();
			receiptPrinterService = null;
		}

		if (printerType.equals(PrinterType.Barcode) && printerDiscoverHelper != null) {
			printerDiscoverHelper.stopDiscover();
			printerDiscoverHelper = null;
		}

		context.unregisterReceiver(bluetoothBroadcastReceiver);
	}
}
