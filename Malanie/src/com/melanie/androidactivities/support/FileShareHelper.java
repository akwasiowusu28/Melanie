package com.melanie.androidactivities.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.melanie.support.CodeStrings;
import com.planetarydoom.barcode.core.WriterException;

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
		((Activity) context).startActivity(Intent.createChooser(intent, "Share"));

	}

	private Uri saveBarcodeBitmapAndReturnUri(String imageName, String barcode) {
		Uri fileUrl = null;
		try {
			String fileName = noSpecialChars(imageName) + CodeStrings.PNG;
			String barcodeString = barcode + String.valueOf(Utils.getCheckSumDigit(barcode));
			Bitmap barcodeBitmap = new MelanieBarcodeEncoder().generateEAN13Barcode(barcodeString);

			if (barcodeCacheFolder == null) {
				barcodeCacheFolder = new File(context.getCacheDir(), CodeStrings.BARCODE_PATH);
				barcodeCacheFolder.mkdirs();
			}
			File barcodefilePath = new File(barcodeCacheFolder, fileName);
			FileOutputStream stream = new FileOutputStream(barcodefilePath);
			barcodeBitmap.compress(CompressFormat.PNG, 100, stream);

			fileUrl = FileProvider.getUriForFile(context, CodeStrings.AUTHORITY, barcodefilePath);
		} catch (FileNotFoundException | WriterException e) {
			// TODO log it
			e.printStackTrace();
		}
		return fileUrl;
	}

	private String noSpecialChars(String value) {
		return value.replaceAll("[!@#$%\\^&*\\( \\)\\.\\,'\"\\\\?\\-/\\|_\\[\\+\\+`~]", "");
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
}
