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
import com.melanie.support.MelanieOperationCallBack;
import com.zj.btsdk.BluetoothService;

@SuppressWarnings("unchecked")
public class MelaniePrinterDiscoverer {

	private final int BLUETOOTH_REQUEST_CODE = 208;

	private MelanieOperationCallBack<Map<String, String>> operationCallBack;
	private LWPrintDiscoverPrinter printerDiscoverHelper;
	private BluetoothAdapter bluetoothAdapter = null;
	private BluetoothService receiptPrinterService;
	private PrinterType printerType;
	private Context context;

	public MelaniePrinterDiscoverer(Context context,
			MelanieOperationCallBack<Map<String, String>> operationCallBack,
			PrinterType printerType) {

		this.operationCallBack = operationCallBack;
		this.context = context;
		this.printerType = printerType;

		setupBluetooth();
	}

	private void setupBluetooth() {
		if (canConnectBluetooth()) {
			enableBluetooth();
			context.registerReceiver(bluetoothBroadcastReceiver,
					new IntentFilter(BluetoothDevice.ACTION_FOUND));
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
		Intent enableBluetoothIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity) context).startActivityForResult(enableBluetoothIntent,
				BLUETOOTH_REQUEST_CODE);
	}

	public void discoverBarcodePrinter() {

		EnumSet<LWPrintDiscoverConnectionType> flag = EnumSet
				.of(LWPrintDiscoverConnectionType.ConnectionTypeBluetooth);
		printerDiscoverHelper = new LWPrintDiscoverPrinter(null, null, flag);
		printerDiscoverHelper.setCallback(discoverBarcodePrinterCallBack);
		printerDiscoverHelper.startDiscover(context);
	}

	private LWPrintDiscoverPrinterCallback discoverBarcodePrinterCallBack = new LWPrintDiscoverPrinterCallback() {
		@Override
		public void onFindPrinter(LWPrintDiscoverPrinter arg0,
				Map<String, String> printerInfo) {
			if (printerDiscoverHelper != null)
				printerDiscoverHelper.stopDiscover();

			operationCallBack.onOperationSuccessful(printerInfo);
		}

		@Override
		public void onRemovePrinter(LWPrintDiscoverPrinter arg0,
				Map<String, String> arg1) {
		}
	};

	private BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (printerType.equals(PrinterType.Receipt)) {
				String action = intent.getAction();

				switch (action) {
				case BluetoothDevice.ACTION_FOUND:

					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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

	public void discoverReceiptPrinter() {
		receiptPrinterService = new BluetoothService(context, new Handler());
		receiptPrinterService.startDiscovery();
	}

	public void clearResources() {
		if (receiptPrinterService.isDiscovering())
			receiptPrinterService.cancelDiscovery();

		receiptPrinterService.stop();
		receiptPrinterService = null;
	}
}
