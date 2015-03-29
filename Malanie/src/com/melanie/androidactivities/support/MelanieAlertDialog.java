package com.melanie.androidactivities.support;

import com.melanie.androidactivities.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * A Custom AlertDialog for use in the whole Melanie app when an alert is needed
 * 
 * @author Akwasi Owusu
 * 
 */
public class MelanieAlertDialog extends AlertDialog.Builder {

	private MelanieAlertDialogButtonModes buttonsMode;
	private ButtonMethods buttonMethods;

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
	public MelanieAlertDialog(Context context,
			MelanieAlertDialogButtonModes buttonsMode,
			ButtonMethods buttonMethods) {
		super(context);
		this.buttonsMode = buttonsMode;
		this.buttonMethods = buttonMethods;
		setupButtons();
	}

	private void setupButtons() {
		this.setPositiveButton(R.string.yes, dialogButtonClickListener);
		this.setNegativeButton(R.string.no, dialogButtonClickListener);
		if (buttonsMode == MelanieAlertDialogButtonModes.YES_NO_CANCEL)
			this.setNeutralButton(R.string.cancel, dialogButtonClickListener);
	}

	private OnClickListener dialogButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			switch (whichButton) {
			case DialogInterface.BUTTON_POSITIVE:
				buttonMethods.yesButtonOperation();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				buttonMethods.noButtonOperation();
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				buttonMethods.cancelButtonOperation();
				dialog.cancel();
				break;
			}
		}
	};

	public static abstract class ButtonMethods {
		/**
		 * The operaion to perform when the yes button on the MelanieAlertDialog
		 * is clicked
		 */
		public abstract void yesButtonOperation();

		/**
		 * The operaion to perform when the no button on the MelanieAlertDialog
		 * is clicked
		 */
		public abstract void noButtonOperation();

		/**
		 * Override this if you want to provide a custom implementation of a
		 * cancel operation when the cancel button is clicked
		 */
		public void cancelButtonOperation() {
		}
	}

	/**
	 * The different Button modes used in a MelanieAlertDialog
	 * 
	 * @author Akwasi Owusu
	 * 
	 */
	public enum MelanieAlertDialogButtonModes {
		YES_NO, YES_NO_CANCEL
	}

}
