package com.melanie.androidactivities.support;

import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;

import com.planetarydoom.barcode.core.BarcodeFormat;
import com.planetarydoom.barcode.core.BitMatrix;
import com.planetarydoom.barcode.core.EAN13Writer;
import com.planetarydoom.barcode.core.EncodeHintType;
import com.planetarydoom.barcode.core.Writer;
import com.planetarydoom.barcode.core.WriterException;

public class MelanieBarcodeEncoder {

	private Bitmap ImageBitmap;
	private String characterEncoding = "ISO-8859-1";
	private int bitmapHeight = 250, bitmapWidth = 250;

	public Bitmap generateEAN13Barcode(String data,
			BarcodeFormat mBarcodeFormat, Map<EncodeHintType, ?> hints)
			throws WriterException {
		Writer writer = null;

		writer = new EAN13Writer();

		String finaldata = Uri.encode(data, characterEncoding);
		BitMatrix bm = writer.encode(finaldata, mBarcodeFormat, bitmapWidth,
				bitmapHeight);
		ImageBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
				Config.ARGB_8888);

		for (int i = 0; i < bitmapWidth; i++)
			for (int j = 0; j < bitmapHeight; j++)
				ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK
						: Color.WHITE);

		return ImageBitmap;
	}
}
