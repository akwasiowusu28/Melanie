package com.melanie.androidactivities.support;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.melanie.androidactivities.R;
import com.melanie.support.OperationResult;

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

	public static void makeToastBasedOnOperationResult(Context context,
			OperationResult result, int successStringId, int failureStringId) {
		if (result.equals(OperationResult.SUCCESSFUL))
			Toast.makeText(context, successStringId, Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, failureStringId, Toast.LENGTH_LONG).show();
	}
	
	public static void makeToast(Context context, int stringId){
		Toast.makeText(context, R.string.printerNotFound, Toast.LENGTH_LONG)
		.show();
	}
}
