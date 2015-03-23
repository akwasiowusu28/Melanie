package com.melanie.androidactivities.support;

import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public final class Utils {

	private static final Handler handler = new Handler();

	public static String generateBarcodeString(int lastItemId, int categoryId) {
		lastItemId++;
		int trailingZeroes = 12 - String.valueOf(categoryId).length();
		String format = "%0" + trailingZeroes + "d";
		String barcodeNumber = categoryId + String.format(format, lastItemId);
		return barcodeNumber;
	}

	public static <T> void notifyListUpdate(final ArrayAdapter<T> adapter) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	public static void clearTextFields(View... views) {
		for (View view : views) {
			if (view instanceof EditText)
				((EditText) view).setText("");
		}

	}
}
