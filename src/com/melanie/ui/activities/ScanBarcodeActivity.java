package com.melanie.ui.activities;

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
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.melanie.ui.R;
import com.melanie.ui.support.CameraPreview;
import com.melanie.ui.support.Utils;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("deprecation")
public class ScanBarcodeActivity extends Activity {

    static {
        System.loadLibrary("iconv");
    }

    ImageScanner scanner;
    private Camera camera;
    private CameraPreview cameraPreview;
    private Handler handler;
    private List<String> scannedBarcodes;
    private MediaPlayer mediaPlayer;
    private boolean isCameraReleased;
    private HashMap<String, Integer> scannedCount;
    PreviewCallback previewCallBack = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            int result = scanAndReturnBarcodeResult(data, camera);

            if (result != 0) {
                resetCameraPreviewCallBack();
                addScannerResultsToScannedBarcodes();
            }
        }

        private int scanAndReturnBarcodeResult(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);
            int result = scanner.scanImage(barcode);

            return result;
        }

        private void playBeep() {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }

        private void addScannerResultsToScannedBarcodes() {
            SymbolSet syms = scanner.getResults();
            for (Symbol sym : syms) {
                String barcode = sym.getData();
                if (sym.getType() == Symbol.EAN13
                        && isValidMelanieBarcode(barcode)) {
                    scannedBarcodes.add(barcode);
                    keepScannedCountForBarcode(barcode);
                    updatePreviewText(barcode);
                    playBeep();
                    break;
                }
            }
        }

        private void keepScannedCountForBarcode(String barcode) {
            if (scannedCount.containsKey(barcode)) {
                int count = scannedCount.get(barcode);
                scannedCount.put(barcode, ++count);
            } else {
                scannedCount.put(barcode, 1);
            }
        }

        private void updatePreviewText(String barcode) {
            TextView textView = (TextView) findViewById(R.id.barcodeDigitsTextView);
            String barcodeAndCount = barcode + " x" + String.valueOf(scannedCount.get(barcode));
            textView.setText(barcodeAndCount);
        }

        private boolean isValidMelanieBarcode(String barcode) {

            boolean isValid = false;
            String barcodePrefix = Utils.getBarcodePrefix();
            if (barcode.substring(0, barcodePrefix.length()).equals(
                    barcodePrefix)) {

                String firstTwelveDigits = barcode.substring(0, barcode.length() - 1);
                int checksum_digit = Utils.getCheckSumDigit(firstTwelveDigits);

                int lastDigit = Integer.parseInt(barcode.substring(barcode.length() - 1));
                isValid = lastDigit == checksum_digit;
            }
            return isValid;
        }

    };
    private Runnable doAutoFocus = new Runnable() {
        @Override
        public void run() {
            if (camera != null) {
                camera.autoFocus(autoFocusCallBack);
            }
        }
    };
    AutoFocusCallback autoFocusCallBack = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            handler.postDelayed(doAutoFocus, 1000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_barcode);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initializeFields();

        initializeMediaPlayer();
        initializeCamera();
        initializeBarcodeScanner();
        loadScannerPreview();

        configurePreviewFrame();

    }

    private void initializeFields() {
        handler = new Handler();
        scannedBarcodes = new ArrayList<String>();
        isCameraReleased = false;
        scannedCount = new HashMap<>();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initializeMediaPlayer() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                mediaPlayer = MediaPlayer.create(ScanBarcodeActivity.this,
                        R.raw.barcodebeep);
                return null;
            }
        };
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            task.execute();
        } else {
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, (Void[]) null);
        }
    }

    private void initializeCamera() {

        camera = getCameraInstance();
        if (camera.getParameters().getSupportedFlashModes() != null) {
            camera.getParameters().setFlashMode(Parameters.FLASH_MODE_AUTO);
        }
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) { // log that you can't get the camera
        }
        return c;
    }

    private void initializeBarcodeScanner() {
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        scanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
        scanner.setConfig(Symbol.EAN13, Config.ENABLE, 1);
    }

    private void loadScannerPreview() {
        cameraPreview = new CameraPreview(this, camera, previewCallBack,
                autoFocusCallBack);
        FrameLayout previewFrame = (FrameLayout) findViewById(R.id.scannerMainView);
        previewFrame.addView(cameraPreview);
    }

    private void configurePreviewFrame() {
        FrameLayout previewFrame = (FrameLayout) findViewById(R.id.scannerMainView);
        previewFrame.addView(redLineView());
        previewFrame.addView(doneButton());
        findViewById(R.id.scannedBarcodeDisplay).bringToFront();
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
                intent.putStringArrayListExtra(LocalConstants.BARCODES,
                        new ArrayList<String>(scannedBarcodes));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void resetCameraPreviewCallBack() {
        cameraPreview.removePreviewCallBack();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (!isCameraReleased) {
                    cameraPreview.resetPreviewCallBack();
                }
            }
        }, 900);
    }

    private void releaseCamera() {
        if (camera != null) {
            SurfaceHolder previewHolder = cameraPreview.getHolder();
            if (previewHolder != null) {
                previewHolder.removeCallback(cameraPreview);
            }
            camera.autoFocus(null);
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            isCameraReleased = true;
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

    private class LocalConstants {
        public static final String BARCODES = "barcodes";
    }
}
