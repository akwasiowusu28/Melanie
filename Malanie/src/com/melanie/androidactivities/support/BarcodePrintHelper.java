package com.melanie.androidactivities.support;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.SparseIntArray;

import com.epson.lwprint.sdk.LWPrint;
import com.epson.lwprint.sdk.LWPrintCallback;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.epson.lwprint.sdk.LWPrintPrintingPhase;
import com.melanie.androidactivities.R;
import com.melanie.androidactivities.SelectPrinterActivity;
import com.melanie.support.CodeStrings;
import com.melanie.support.OperationCallBack;

public class BarcodePrintHelper {

	private Context context;
	private Map<String, String> printerInfo;

	private static final int CUT_EACH_TAPE = 0;
	private static final int DENSITY = -2;

	private LWPrint printer;

	private boolean isPrinterFound = false;

	private boolean bluetoothEnableRefused = false;

	private ProgressDialog progressDialog = null;
	private Handler handler;
	private ScheduledExecutorService executorService;

	private int printPhaseMessage;
	private MelaniePrinterDiscoverer printerDiscoverer;

	private int numberOfBarcodes;
	private boolean isInitialized;

	public BarcodePrintHelper(Context context, boolean bluetoothEnabledRefused) {
		this.isInitialized = !bluetoothEnabledRefused;
		if (!bluetoothEnabledRefused) {
			this.context = context;
			initializePrinter();
			createPrintProgressDialog();
			handler = new Handler(context.getMainLooper());
		}
	}

	private void initializePrinter() {
		if (!bluetoothEnableRefused && printer == null && printerInfo == null) {
			printerDiscoverer = new MelaniePrinterDiscoverer(context, new OperationCallBack<Map<String, String>>() {

				@Override
				public void onOperationSuccessful(Map<String, String> result) {
					printerInfo = result;
					isPrinterFound = true;
				}

			}, PrinterType.Barcode);

			printerDiscoverer.discoverBarcodePrinter();
		}
		printer = new LWPrint(context);
		printer.setCallback(new PrintCallBack());
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
						((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
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
		@SuppressWarnings("unchecked")
		protected Void doInBackground(Object... params) {
			LWPrint printer = (LWPrint) params[PRINTER];

			Map<String, String> printerInfo = (Map<String, String>) params[PRINTER_INFO];
			HashMap<String, Object> printSettings = (HashMap<String, Object>) params[PRINT_SETTINGS];
			AssetManager assetManager = (AssetManager) params[ASSET_MANAGER];
			String barcode = (String) params[BARCODE];

			printer.setPrinterInformation(printerInfo);
			printer.doPrint(new MelanieBarcodeDataProvider(assetManager, barcode, numberOfBarcodes), printSettings);
			return null;
		}
	}

	private void createPrintProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage(context.getText(R.string.printPreparing));
			progressDialog.setMax(100);
			setProgressBarHandlers();
		}
	}

	private void setProgressBarHandlers() {
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getText(R.string.cancel),
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

	private HashMap<String, Object> getPrintSettings() {

		HashMap<String, Object> printSettings = new HashMap<String, Object>();
		printSettings.put(LWPrintParameterKey.Copies, 1);
		printSettings.put(LWPrintParameterKey.HalfCut, false);
		printSettings.put(LWPrintParameterKey.TapeCut, CUT_EACH_TAPE);
		printSettings.put(LWPrintParameterKey.PrintSpeed, false);
		printSettings.put(LWPrintParameterKey.Density, DENSITY);

		return printSettings;
	}

	private void performPrint(String barcode) {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (progressDialog != null) {
					progressDialog.show();
				}
			}
		}, 2);

		new MelaniePrintAsyncTask().execute(printer, printerInfo, getPrintSettings(), context.getAssets(), barcode);

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
						progressDialog.setMessage(context.getText(printPhaseMessage));
					}

				});
			}
		}, 2, 2, TimeUnit.SECONDS);
	}

	public void printBarcode(String barcode, Map<String, String> printerInfo) {
		if (isInitialized) {
			if(printerInfo != null) {
				this.printerInfo = printerInfo;
			}
			if(printerDiscoverer.isBluetoothAvailable() && isPrinterFound) {
				performPrint(barcode);
			} else{
				Intent intent = new Intent(context, SelectPrinterActivity.class);
				intent.putExtra(CodeStrings.PRINTER_TYPE, PrinterType.Barcode.toString());
				((Activity)context).startActivityForResult(intent, Utils.Constants.PRINTER_SELECT_REQUEST_CODE);
			}
		}
	}

	public void setIsPrinterFound(boolean isPrinterFound){
		this.isPrinterFound = isPrinterFound;
	}

	public void clearResources() {
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
