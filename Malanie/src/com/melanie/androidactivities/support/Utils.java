package com.melanie.androidactivities.support;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.melanie.androidactivities.MainActivity;
import com.melanie.androidactivities.R;
import com.melanie.support.OperationResult;

/**
 * 
 * @author Akwasi Owusu This class is a utility class that performs various
 *         functions used across multiple activities
 */
public final class Utils {

	public static class Costants {
		public static final String CustomerId = "CustomerId";
		public static final String BARCODES = "barcodes";
		public static final String EMPTY_STRING = "";
	}

	/**
	 * Updates a listview to reflect changes when items are added or deleted
	 * 
	 * @param adapter
	 *            the adapter whose list should be updated
	 * @param handler
	 *            the handler associated with the UI thread invoking this method
	 */
	public static void notifyListUpdate(final BaseAdapter adapter,
			Handler handler) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (adapter != null)
					adapter.notifyDataSetChanged();
			}
		});
	}

	public static String getBarcodePrefix() {
		return "2801989";
	}

	/**
	 * Clears any number of text fields supplied
	 * 
	 * @param views
	 */
	public static void clearInputTextFields(View... views) {
		for (View view : views)
			if (view instanceof EditText)
				((EditText) view).setText("");

	}

	/**
	 * Reset the text of a set of EditText and TextView fields to 0.00
	 * 
	 * @param views
	 *            The views to reset
	 */
	public static void resetTextFieldsToZeros(View... views) {
		for (View view : views)
			if (view instanceof EditText)
				((EditText) view).setText(R.string.amountZeroes);
			else if (view instanceof TextView)
				((TextView) view).setText(R.string.amountZeroes);
	}

	/**
	 * Common Toast maker for all of Melanie based on OperationResults
	 * 
	 * @param context
	 *            The activity that wants to make a toast
	 * @param result
	 *            One of two OperationResult enums based on which toast string
	 *            will be shown
	 * @param successStringId
	 *            The string to display on toast when OperationResult is
	 *            SUCESSFUL
	 * @param failureStringId
	 *            The string to display on toast when OperationResult is FAILED
	 */
	public static void makeToastBasedOnOperationResult(Context context,
			OperationResult result, int successStringId, int failureStringId) {

		int toastString = result.equals(OperationResult.FAILED) ? failureStringId
				: successStringId;

		Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
	}

	/**
	 * General Toast maker for all of Melanie
	 * 
	 * @param context
	 *            The activity that wants to make a toast
	 * @param stringId
	 *            The string to display on toast
	 */
	public static void makeToast(Context context, int stringId) {
		Toast.makeText(context, stringId, Toast.LENGTH_LONG).show();
	}

	public static <T> List<T> removeDuplicates(List<T> items) {
		List<T> tempItems = new ArrayList<T>();
		tempItems.addAll(new HashSet<T>(items));
		return tempItems;
	}

	public static <T> void mergeItems(List<T> sourceItems, List<T> targetItems) {
		for (T item : sourceItems)
			if (!targetItems.contains(item))
				targetItems.add(item);

		Iterator<T> iterator = targetItems.iterator();
		while (iterator.hasNext()) {
			T item = iterator.next();
			if (!sourceItems.contains(item))
				iterator.remove();
		}
	}

	public static int getTextColor(Context context) {
		return context instanceof MainActivity ? Color.WHITE : Color.BLACK;
	}

	public static <T> Map<T, Integer> groupItems(List<T> items) {
		Map<T, Integer> itemGroup = new HashMap<T, Integer>();

		for (T item : items)
			if (!itemGroup.containsKey(item))
				itemGroup.put(item, 1);
			else {
				Integer itemCount = itemGroup.get(item);
				itemCount++;
				itemGroup.put(item, itemCount);
			}

		return itemGroup;
	}

	public static Date getDateOnly(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
}
