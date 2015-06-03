package com.melanie.androidactivities.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.melanie.androidactivities.R;

/**
 * A Custom AlertDialog for use in the whole Melanie app when an alert is needed
 * 
 * @author Akwasi Owusu
 * 
 */
public class MelanieAlertDialog extends AlertDialog.Builder {

	private ButtonModes buttonsMode;
	private ButtonMethods buttonMethods;
	private boolean isOkCancel = false;

	/**
	 * Instantiates the MelanieAlertDialog with the required button options and
	 * their corresponding methods to invoke when clicked
	 * 
	 * @param context
	 *            The activity that creates this dialog
	 * @param buttonsMode
	 *            One of the MelanieAlertDialogButtonsMode enumeration: YES_NO
	 *            and YES_NO_CANCEL
	 * @param buttonMethods
	 */
	public MelanieAlertDialog(Context context, ButtonModes buttonsMode, ButtonMethods buttonMethods) {
		super(context);
		this.buttonsMode = buttonsMode;
		this.buttonMethods = buttonMethods;
		isOkCancel = buttonsMode.equals(ButtonModes.OK_CANCEL);
		setupButtons();
	}

	private void setupButtons() {
		this.setPositiveButton(isOkCancel ? android.R.string.ok : R.string.yes, dialogButtonClickListener);
		if(buttonsMode == ButtonModes.YES_NO || buttonsMode == ButtonModes.YES_NO_CANCEL) {
			this.setNegativeButton(R.string.no, dialogButtonClickListener);
		}
		if (buttonsMode == ButtonModes.YES_NO_CANCEL || buttonsMode == ButtonModes.OK_CANCEL) {
			this.setNeutralButton(R.string.cancel, dialogButtonClickListener);
		}
	}

	private OnClickListener dialogButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			switch (whichButton) {
			case DialogInterface.BUTTON_POSITIVE:
				if (isOkCancel) {
					buttonMethods.okButtonOperation();
				} else {
					buttonMethods.yesButtonOperation();
				}
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				buttonMethods.noButtonOperation();
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				buttonMethods.cancelButtonOperation();
				break;
			default:
				buttonMethods.cancelButtonOperation();
				dialog.cancel();
			}
		}
	};

	public static abstract class ButtonMethods {
		/**
		 * The operaion to perform when the yes button on the MelanieAlertDialog
		 * is clicked. Override this to provide a custom implementation of a Yes
		 * operation
		 */
		public void yesButtonOperation() {
		}

		/**
		 * The operaion to perform when the no button on the MelanieAlertDialog
		 * is clicked. Override this to provide a custom implementation of a No
		 * operation
		 */
		public void noButtonOperation() {
		}

		/**
		 * Override this if you want to provide a custom implementation of a
		 * cancel operation when the cancel button is clicked
		 */
		public void cancelButtonOperation() {
		}

		/**
		 * Override this if you want to provide a custom implementation of an OK
		 * operation when the cancel button is clicked
		 */
		public void okButtonOperation() {
		}
	}

	/**
	 * The different Button modes used in a MelanieAlertDialog
	 * 
	 * @author Akwasi Owusu
	 * 
	 */
	public enum ButtonModes {
		YES_NO, YES_NO_CANCEL, OK_CANCEL
	}

}
