package com.melanie.androidactivities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.UserController;
import com.melanie.entities.User;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;

public class SignupActivity extends ActionBarActivity {

	private UserController userController;
	EditText passwordField;
	EditText confirmPasswordField;
	EditText nameField;
	EditText phoneField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		initializeFields();
		setupCreateAccountButton();
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
		String phone = phoneField.getText().toString();

		if (passwordsMatch(password, confirmPassword))
			createUser(name, phone, password);
		else {
			switchPasswordFieldsBackColor(false);
			Utils.makeToast(this, R.string.passwordsNotMatch);
		}

	}

	private void createUser(String name, String phone, String password) {
		userController.createUser(name, phone, password,
				new MelanieOperationCallBack<User>() {

					@Override
					public void onOperationSuccessful(User result) {
						switchPasswordFieldsBackColor(true);
						Utils.makeToast(SignupActivity.this,
								R.string.createAccountSuccess);
						clearFields();
					}

					@Override
					public void onOperationFailed(Throwable e) {
						Utils.makeToast(SignupActivity.this,
								R.string.createAccountFailed);
					}
				});
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
			passwordField.setBackgroundColor(Color.WHITE);
			confirmPasswordField.setBackgroundColor(Color.WHITE);
		} else {
			passwordField.setBackgroundColor(Color.rgb(250, 213, 182));
			confirmPasswordField.setBackgroundColor(Color.rgb(250, 213, 182));
		}
	}
}
