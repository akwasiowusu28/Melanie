package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;

import com.melanie.androidactivities.support.CameraPreview;

public class ScanBarcodeActivity extends Activity {

	private Camera camera;
	private CameraPreview cameraPreview;
	private Handler autoFocusHandler;
	private List<String> scannedBarcodes;
	ImageScanner scanner;

	static {
		System.loadLibrary("iconv");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scan_barcode);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		scannedBarcodes = new ArrayList<String>();
		
		initializeCamera();

		initializeBarcodeScanner();

		loadScannerPreview();

		FrameLayout f = (FrameLayout) findViewById(R.id.scannerMainView);
		f.addView(redLineView());
	}

	private void initializeBarcodeScanner() {
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);
	}

	private void loadScannerPreview() {
		cameraPreview = new CameraPreview(this, camera, previewCallBack,
				autoFocusCallBack);
		FrameLayout preview = (FrameLayout) findViewById(R.id.scannerMainView);
		preview.addView(cameraPreview);

		camera.setPreviewCallback(previewCallBack);
		camera.startPreview();
		camera.autoFocus(autoFocusCallBack);
	}

	private void initializeCamera() {
		autoFocusHandler = new Handler();
		camera = getCameraInstance();
		if (camera.getParameters().getSupportedFlashModes() != null)
			camera.getParameters().setFlashMode(Parameters.FLASH_MODE_AUTO);
	}

	private Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private View redLineView() {
		View view = new View(this);
		view.setBackgroundColor(Color.RED);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 3);
		params.gravity = Gravity.CENTER_VERTICAL;
		view.setLayoutParams(params);
		view.bringToFront();
		return view;
	}

	PreviewCallback previewCallBack = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getSupportedPreviewSizes().get(0);

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				camera.setPreviewCallback(null);
				// mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					String barcodeDigits = sym.getData();
					scannedBarcodes.add(barcodeDigits);
				}
			}
		}
	};

	AutoFocusCallback autoFocusCallBack = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (camera != null)
				camera.autoFocus(autoFocusCallBack);
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}
}
