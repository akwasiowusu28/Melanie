package com.melanie.androidactivities;

import java.util.Calendar;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.MelanieSession;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.entities.User;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SignupActivity extends Activity {

	private UserController userController;
	private EditText passwordField;
	private EditText confirmPasswordField;
	private EditText nameField;
	private EditText phoneField;
    private String phoneNumber;
    private String confirmCode;
    
    private MelanieSession business;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		initializeFields();
		setupCreateAccountButton();
		business = MelanieBusinessFactory.getSession();
		
		DataSource dataSource = OpenHelperManager.getHelper(getBaseContext(), DataSource.class);
		
		// ORMLite
		business.initialize(dataSource);
		
		// Backendless
		business.initializeCloud(this);
	}

	private void initializeFields() {
		userController = MelanieBusinessFactory.makeUserController();
		passwordField = (EditText) findViewById(R.id.password);
		confirmPasswordField = (EditText) findViewById(R.id.confirmPassword);
		nameField = (EditText) findViewById(R.id.userName);
		phoneField = (EditText) findViewById(R.id.phoneNumber);
	}

	private void setupCreateAccountButton() {
		Button createAccountButton = (Button) findViewById(R.id.createAccountButton);
		createAccountButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createAccount();
			}
		});

	}

	private void createAccount() {

		String password = passwordField.getText().toString();
		String confirmPassword = confirmPasswordField.getText().toString();
		String name = nameField.getText().toString();
	    phoneNumber = phoneField.getText().toString();

		if (passwordsMatch(password, confirmPassword))
			createUser(name, phoneNumber, password);
		else {
			switchPasswordFieldsBackColor(false);
			Utils.makeToast(this, R.string.passwordsNotMatch);
		}

	}

	private void createUser(String name, String phone, String password) {
		try {
			userController.createUser(name, phone, password, getDeviceId(),
					new MelanieOperationCallBack<User>() {

						@Override
						public void onOperationSuccessful(User result) {
							switchPasswordFieldsBackColor(true);
							clearFields();
						}

						@Override
						public void onOperationFailed(Throwable e) {
							Utils.makeToast(SignupActivity.this,
									R.string.createAccountFailed);
						}
					});
		} catch (MelanieBusinessException e) {
			// TODO log it
			e.printStackTrace();
		}
		sendConfirmSMS();
	}

	private String getDeviceId(){
		TelephonyManager telephonyManager = (TelephonyManager)getBaseContext()
				                        .getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = telephonyManager.getDeviceId();
		return deviceId;
	}
	
	private boolean passwordsMatch(String password, String confirmPassword) {
		return password.equals(confirmPassword);
	}

	private void clearFields() {
		nameField.setText(Utils.Constants.EMPTY_STRING);
		passwordField.setText(Utils.Constants.EMPTY_STRING);
		confirmPasswordField.setText(Utils.Constants.EMPTY_STRING);
		phoneField.setText(Utils.Constants.EMPTY_STRING);
	}

	private void switchPasswordFieldsBackColor(boolean isValid) {
		if (isValid) {
			passwordField.setBackgroundResource(android.R.drawable.edit_text);
			confirmPasswordField.setBackgroundResource(android.R.drawable.edit_text);
		} else {
			passwordField.setBackgroundColor(Color.rgb(250, 213, 182));
			confirmPasswordField.setBackgroundColor(Color.rgb(250, 213, 182));
		}
	}
	
	private void sendConfirmSMS(){
		generateConfirmCode();
		
		Intent intent = new Intent(this,ConfirmActivity.class);
		intent.putExtra(Utils.Constants.CONFIRM_CODE, confirmCode);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 28, 
				      intent, 0);
		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage(phoneNumber, null, getConfirmMessage(), pendingIntent, null);
	}
	
	private String getConfirmMessage(){
		return Utils.Constants.CONFIRM_SMS_MESSAGE + confirmCode;
	}
	
	private void generateConfirmCode(){
		String currentTimeString = String.valueOf(Calendar.getInstance().getTimeInMillis());
		confirmCode = currentTimeString.substring(currentTimeString.length() - 4);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
}
