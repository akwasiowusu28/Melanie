package com.melanie.androidactivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.melanie.androidactivities.support.MelanieAlertDialog;
import com.melanie.androidactivities.support.MelanieAlertDialog.MelanieAlertDialogButtonModes;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.UserController;
import com.melanie.entities.User;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class LoginActivity extends  Activity {

	private UserController userController;
	private MelanieAlertDialog differntDeviceAlertDialog;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initializeFields();
		setupSignupButton();
		setupLoginButton();
		setupDifferntDeviceAlertDialog();
		
	}
	
	private void initializeFields() {
		userController = MelanieBusinessFactory.makeUserController();
	}

	private void setupSignupButton() {
		Button button = (Button) findViewById(R.id.loginSignupButton);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						SignupActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setupLoginButton() {
		Button button = (Button) findViewById(R.id.loginButton);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginAsync.execute(null,null,null);
 			}
		});
	}

	private void setupProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage(getText(R.string.pleaseWait));
		}
	}

	private void login() {
		EditText phoneTextField = (EditText) findViewById(R.id.loginPhoneNumber);
		EditText passwordTextField = (EditText) findViewById(R.id.loginPassword);
		String phoneNumber = phoneTextField.getText().toString();
        final String password = passwordTextField.getText().toString();
		if (userController != null) {
			try {
				userController.checkUserExistOnCloud(phoneNumber,
						new MelanieOperationCallBack<User>() {

							@Override
							public void onOperationSuccessful(User user) {
								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								if (user != null) {
									user.setPassword(password);
									handleUserExists(user);
								} else
									Utils.makeToast(LoginActivity.this,
											R.string.accountLookupFailed);
							}
						});
			} catch (MelanieBusinessException e) {
				// TODO Log it
				e.printStackTrace();
			}
		}
	}

	private void handleUserExists(User user) {
		if (user.getDeviceId().equals(getCurrentDeviceId())) {
			userController.login(user,
					new MelanieOperationCallBack<OperationResult>() {

						@Override
						public void onOperationSuccessful(OperationResult result) {
							if (result.equals(OperationResult.SUCCESSFUL)) {
								Intent intent = new Intent(LoginActivity.this,
										MainActivity.class);
								startActivity(intent);
								finish();
							} else
								Utils.makeToast(LoginActivity.this,
										R.string.loginFailed);
						}
					});
		} else {
			differntDeviceAlertDialog.show();
		}
	}

	private void setupDifferntDeviceAlertDialog() {
		differntDeviceAlertDialog = makeAlertDialog();
		differntDeviceAlertDialog
				.setTitle(getString(R.string.differentDevices));
		differntDeviceAlertDialog
				.setMessage(getString(R.string.accountExistForOtherDevice));
		differntDeviceAlertDialog.create();
	}

	private MelanieAlertDialog makeAlertDialog() {
		return new MelanieAlertDialog(this,
				MelanieAlertDialogButtonModes.YES_NO,
				new MelanieAlertDialog.ButtonMethods() {

					@Override
					public void yesButtonOperation() {

					}

					@Override
					public void noButtonOperation() {

					}
				});
	}

	private String getCurrentDeviceId() {
		TelephonyManager telephonyManager = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = telephonyManager.getDeviceId();
		return deviceId;
	}

	private final AsyncTask<Void, Void, Void> loginAsync = new AsyncTask<Void, Void, Void>() {

		@Override
		protected void onPreExecute() {
			setupProgressDialog();
			if (progressDialog != null)
				progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			login();
			return null;
		}
	};
}
