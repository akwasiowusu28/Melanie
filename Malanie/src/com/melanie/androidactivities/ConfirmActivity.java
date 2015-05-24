package com.melanie.androidactivities;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.melanie.androidactivities.support.MelanieAlertDialog;
import com.melanie.androidactivities.support.MelanieAlertDialog.MelanieAlertDialogButtonModes;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.MelanieSession;
import com.melanie.business.UserController;
import com.melanie.support.CodeStrings;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class ConfirmActivity extends AppCompatActivity {

	private static final String IS_MESSAGE_SENT = "isMessageSent";
	
	private Button confirmButton;
	private String confirmCode;
	private UserController userController;
	private MelanieAlertDialog confirmAlertDialog;
	private String phoneNumber;
	private boolean confirmFieldsDisabled;
	private EditText confirmTextField;
	private TextView confirmLabel;
	private Button sendCodeButton;
	private boolean isMessageSent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm);
		if(savedInstanceState != null){
			isMessageSent =savedInstanceState.getBoolean(IS_MESSAGE_SENT);
		}
		initializeFields();
		setupConfirmButton();
		showConfirmAlertDialog();
		setupSendConfirmationCodeButton();
	}

	private void initializeFields() {
		userController = MelanieBusinessFactory.makeUserController();
		Intent intent = getIntent();
		phoneNumber = intent.getStringExtra(Utils.Constants.PHONE_NUMBER);
		confirmFieldsDisabled = false;
		confirmTextField = (EditText) findViewById(R.id.confirmTextField);
		confirmLabel = (TextView) findViewById(R.id.confirmLabel);
		sendCodeButton = (Button) findViewById(R.id.sendConfirmCode);

	}

	private void setupConfirmButton() {
		confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (updateUser().equals(OperationResult.SUCCESSFUL)) {
					launchMainActivity();
				}
			}
		});
	}

	private void setupSendConfirmationCodeButton() {
		sendCodeButton = (Button) findViewById(R.id.sendConfirmCode);
		sendCodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmAlertDialog.show();
			}
		});
	}

	private void showConfirmAlertDialog() {
		confirmAlertDialog = makeAlertDialog();
		confirmAlertDialog.setTitle(getString(R.string.confirmNumber));
		confirmAlertDialog
				.setMessage(getString(R.string.confirmNumberQuestion));
		confirmAlertDialog.show();
	}

	private MelanieAlertDialog makeAlertDialog() {
		return new MelanieAlertDialog(this,
				MelanieAlertDialogButtonModes.YES_NO,
				new MelanieAlertDialog.ButtonMethods() {

					@Override
					public void yesButtonOperation() {
						sendConfirmSMS();
						if (confirmFieldsDisabled) {
							changeConfirmFieldsVisibility(true);
							sendCodeButton.setVisibility(View.GONE);

						}
					}

					@Override
					public void noButtonOperation() {
						confirmFieldsDisabled = true;
						changeConfirmFieldsVisibility(false);
						sendCodeButton.setVisibility(View.VISIBLE);
						this.cancelButtonOperation();
					}
				});
	}

	private void changeConfirmFieldsVisibility(boolean makeVisible) {
		if (makeVisible) {
			confirmButton.setVisibility(View.VISIBLE);
			confirmTextField.setVisibility(View.VISIBLE);
			confirmLabel.setVisibility(View.VISIBLE);
		} else {
			confirmButton.setVisibility(View.GONE);
			confirmTextField.setVisibility(View.GONE);
			confirmLabel.setVisibility(View.GONE);
		}
	}

	private OperationResult updateUser() {
		OperationResult result = OperationResult.FAILED;
		String enteredConfirmCode = ((EditText) findViewById(R.id.confirmTextField))
				.getText().toString();
		if (enteredConfirmCode.equals(confirmCode)) {
			try {
				MelanieSession session = MelanieBusinessFactory.getSession();
				userController.updateUser(session.getUser(), CodeStrings.ISCONFIRMED, true,null);
			} catch (MelanieBusinessException e) {
				// TODO log it
				e.printStackTrace();
			}
			result = OperationResult.SUCCESSFUL;
		}
		return result;
	}

	private void sendConfirmSMS() {
		generateConfirmCode();

		//Intent intent = new Intent(this,null);

		//PendingIntent pendingIntent = PendingIntent.getActivity(this, 28,
		//		intent, 0);
		isMessageSent = true;
		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage(phoneNumber, null, getConfirmMessage(),
				null, null);
	}

	private String getConfirmMessage() {
		return Utils.Constants.CONFIRM_SMS_MESSAGE + confirmCode;
	}

	private void generateConfirmCode() {
		String currentTimeString = String.valueOf(Calendar.getInstance()
				.getTimeInMillis());
		confirmCode = currentTimeString
				.substring(currentTimeString.length() - 4);
	}

	private void launchMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean(IS_MESSAGE_SENT, isMessageSent);
		super.onSaveInstanceState(savedInstanceState);
	}
}
