package com.melanie.androidactivities.support;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

	/**
	 * Reset the text of a set of EditText and TextView fields to 0.00
	 * 
	 * @param views
	 *            The views to reset
	 */
	public static void resetTextFieldsToZeros(View... views) {
		for (View view : views) {
			if (view instanceof EditText)
				((EditText) view).setText(R.string.amountZeroes);
			else if (view instanceof TextView)
				((TextView) view).setText(R.string.amountZeroes);
		}
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
		Toast.makeText(context, R.string.printerNotFound, Toast.LENGTH_LONG)
				.show();
	}

	/**
	 * Expand a view with a stretch downward animation
	 * 
	 * @param view
	 *            The view to expand
	 */
	public static void expandView(final View view) {
		view.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = view.getMeasuredHeight();

		view.getLayoutParams().height = 0;
		view.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				view.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT
						: (int) (targetHeight * interpolatedTime);
				view.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (targetHeight / view.getContext().getResources()
				.getDisplayMetrics().density));
		view.startAnimation(a);
	}

	/**
	 * Collapse a stretched view
	 * 
	 * @param view
	 *            The view to collapse
	 */
	public static void collapseView(final View view) {
		final int initialHeight = view.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				if (interpolatedTime == 1) {
					view.setVisibility(View.GONE);
				} else {
					view.getLayoutParams().height = initialHeight
							- (int) (initialHeight * interpolatedTime);
					view.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / view.getContext().getResources()
				.getDisplayMetrics().density));
		view.startAnimation(a);
	}

	/**
	 * To align or unalign a view to a RelativeLayout's bottom
	 * 
	 * @param view
	 *            The view to align
	 * @param isAlignToBottom
	 *            flag to determine whether to align to bottom or not
	 */
	public static void AlignOrUnalignViewToParentBottom(View view,
			boolean isAlignToBottom) {

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				view.getLayoutParams());

		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				isAlignToBottom ? RelativeLayout.TRUE : 0);
		view.setLayoutParams(params);
	}
}
