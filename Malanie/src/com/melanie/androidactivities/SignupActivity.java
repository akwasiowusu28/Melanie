package com.melanie.androidactivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.UserController;
import com.melanie.entities.User;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SignupActivity extends AppCompatActivity {

	private UserController userController;
	private EditText passwordField;
	private EditText confirmPasswordField;
	private EditText nameField;
	private EditText phoneField;
    private String phoneNumber;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		initializeFields();
		setupCreateAccountButton();
	}

	private void initializeFields() {
		userController = BusinessFactory.makeUserController();
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
					new OperationCallBack<User>() {

						@Override
						public void onOperationSuccessful(User user) {
							switchPasswordFieldsBackColor(true);
							BusinessFactory.getSession().setUser(user);
							launchMainActivity();
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

	private void switchPasswordFieldsBackColor(boolean isValid) {
		if (isValid) {
			passwordField.setBackgroundResource(android.R.drawable.edit_text);
			confirmPasswordField.setBackgroundResource(android.R.drawable.edit_text);
		} else {
			passwordField.setBackgroundColor(Color.rgb(250, 213, 182));
			confirmPasswordField.setBackgroundColor(Color.rgb(250, 213, 182));
		}
	}
	
	
	private void launchMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
}
