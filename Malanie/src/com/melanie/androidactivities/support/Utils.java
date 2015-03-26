package com.melanie.androidactivities.support;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.melanie.androidactivities.R;
import com.melanie.support.OperationResult;

/**
 * 
 * @author Akwasi Owusu This class is a utility class that performs various
 *         functions used across multiple activities
 */
public final class Utils {

	private static final Handler handler = new Handler();

	/**
	 * Updates a listview to reflect changes when items are added or deleted
	 * 
	 * @param adapter
	 *            the adapter whose list should be updated
	 */
	public static void notifyListUpdate(final BaseAdapter adapter) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * Clears any number of text fields supplied
	 * 
	 * @param views
	 */
	public static void clearInputTextFields(View... views) {
		for (View view : views) {
			if (view instanceof EditText)
				((EditText) view).setText("");
		}

	}

	public static void resetTextFieldsToZeros(View... views) {
		for (View view : views) {
			if (view instanceof EditText)
				((EditText) view).setText(R.string.amountZeroes);
			else if (view instanceof TextView)
				((TextView) view).setText(R.string.amountZeroes);
		}
	}

	public static void makeToastBasedOnOperationResult(Context context,
			OperationResult result, int successStringId, int failureStringId) {
		if (result.equals(OperationResult.SUCCESSFUL))
			Toast.makeText(context, successStringId, Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, failureStringId, Toast.LENGTH_LONG).show();
	}

	public static void makeToast(Context context, int stringId) {
		Toast.makeText(context, R.string.printerNotFound, Toast.LENGTH_LONG)
				.show();
	}
}
