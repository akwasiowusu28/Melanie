package com.melanie.ui.support;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * @author Akwasi Owusu <strong>Uses most of the code from the Zbar sample.
 *         Special thanks to lisah0</strong>
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera camera;
    private PreviewCallback previewCallback;
    private AutoFocusCallback autoFocusCallback;

    public CameraPreview(Context context, Camera camera,
                         PreviewCallback previewCb, AutoFocusCallback autoFocusCb) {
        super(context);

        setWillNotDraw(false);

        this.camera = camera;
        previewCallback = previewCb;
        autoFocusCallback = autoFocusCb;
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            resetPreviewCallBack();
            camera.startPreview();
            camera.autoFocus(autoFocusCallback);

        } catch (IOException e) {
            Log.d("DBG", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Camera preview released in activity
    }

    public void removePreviewCallBack() {
        camera.setPreviewCallback(null);
    }

    public void resetPreviewCallBack() {

        camera.setPreviewCallback(previewCallback);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (mHolder.getSurface() == null)
            return;
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            camera.setDisplayOrientation(90);

            camera.setPreviewDisplay(mHolder);
            resetPreviewCallBack();
            camera.startPreview();
            camera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            Log.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }
}
