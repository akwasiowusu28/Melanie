package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.melanie.androidactivities.support.CameraPreview;

public class ScanBarcodeActivity extends Activity {

	private static final String BARCODE_LIST = "barcodes";

	private Camera camera;
	private CameraPreview cameraPreview;
	private Handler handler;
	private List<String> scannedBarcodes;
	private MediaPlayer mediaPlayer;
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

		initializeFields();

		initializeMediaPlayer();
		initializeCamera();
		initializeBarcodeScanner();
		loadScannerPreview();

		addExtraViewsToPreviewFrame();

	}

	private void initializeFields() {
		handler = new Handler();
		scannedBarcodes = new ArrayList<String>();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initializeMediaPlayer() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				mediaPlayer = MediaPlayer.create(ScanBarcodeActivity.this,
						R.raw.barcodebeep);
				mediaPlayer.setVolume(2,20);
				return null;
			}
		};
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			task.execute();
		else
			task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, (Void[]) null);
	}

	private void initializeCamera() {

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

	private void initializeBarcodeScanner() {
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);
	}

	private void loadScannerPreview() {
		cameraPreview = new CameraPreview(this, camera, previewCallBack,
				autoFocusCallBack);
		FrameLayout previewFrame = (FrameLayout) findViewById(R.id.scannerMainView);
		previewFrame.addView(cameraPreview);
		camera.setPreviewCallback(previewCallBack);
		camera.startPreview();
		camera.autoFocus(autoFocusCallBack);
	}

	private void addExtraViewsToPreviewFrame() {
		FrameLayout previewFrame = (FrameLayout) findViewById(R.id.scannerMainView);
		previewFrame.addView(redLineView());
		previewFrame.addView(doneButton());
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

	private Button doneButton() {
		Button button = new Button(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER;
	    button.setLayoutParams(params);
		button.setText(R.string.doneText);
		setButtonOnclickListner(button);
		return button;
	}

	private void setButtonOnclickListner(Button button) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putStringArrayListExtra(BARCODE_LIST,
						new ArrayList<String>(scannedBarcodes));
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	PreviewCallback previewCallBack = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				camera.setPreviewCallback(null);
				if (mediaPlayer != null) {
					mediaPlayer.reset();
					mediaPlayer.start();
				}
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
			handler.postDelayed(doAutoFocus, 1000);
		}
	};

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (camera != null)
				camera.autoFocus(autoFocusCallBack);
		}
	};

	private void releaseCamera() {
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	private void releaseMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		releaseCamera();
		releaseMediaPlayer();
	}
}
