package com.melanie.ui.support;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.planetarydoom.barcode.core.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileShareHelper {

    private Context context;
    private File barcodeCacheFolder;

    public FileShareHelper(Context context) {
        this.context = context;
    }

    public void shareBarcodeImage(String barcode, String imageName) {

        Uri fileUrl = saveBarcodeBitmapAndReturnUri(imageName, barcode);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, fileUrl);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setDataAndType(fileUrl, context.getContentResolver().getType(fileUrl));

        context.startActivity(Intent.createChooser(intent, "Share"));

    }

    private Uri saveBarcodeBitmapAndReturnUri(String imageName, String barcode) {
        Uri fileUri = null;
        try {
            String fileName = noSpecialChars(imageName) + LocalConstants.PNG;
            String barcodeString = barcode + String.valueOf(Utils.getCheckSumDigit(barcode));
            Bitmap barcodeBitmap = new MelanieBarcodeEncoder().generateEAN13Barcode(barcodeString);

            if (barcodeCacheFolder == null) {
                barcodeCacheFolder = new File(context.getCacheDir(), LocalConstants.BARCODE_PATH);
                if (!barcodeCacheFolder.exists())
                    barcodeCacheFolder.mkdirs();
            }
            File barcodefilePath = new File(barcodeCacheFolder, fileName);
            FileOutputStream stream = new FileOutputStream(barcodefilePath);
            barcodeBitmap.compress(CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

            fileUri = FileProvider.getUriForFile(context, LocalConstants.AUTHORITY, barcodefilePath);
        } catch (WriterException | IOException e) {
            // TODO log it
            e.printStackTrace();
        }
        return fileUri;
    }

    private String noSpecialChars(String value) {
        return value.replaceAll(LocalConstants.SPECIAL_CHARS, LocalConstants.EMPTY_STRING);
    }

    public void performCleanup() {
        if (barcodeCacheFolder != null) {
            File[] files = barcodeCacheFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    private class LocalConstants {
        public static final String BARCODE_PATH = "barcodes/";
        public static final String PNG = ".png";
        public static final String AUTHORITY = "com.melanie.ui.fileprovider";
        public static final String SPECIAL_CHARS = "[!@#$%\\^&*\\( \\)\\.\\,'\"\\\\?\\-/\\|_\\[\\+\\+`~]";
        public static final String EMPTY_STRING = "";
    }
}
