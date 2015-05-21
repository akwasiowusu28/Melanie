package com.melanie.androidactivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.UserController;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;

public class LoginActivity extends ActionBarActivity {

	private UserController userController;
	private static final String NONE ="none";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initializeFields();
		setupSignupButton();
	}

	private void initializeFields(){
		userController = MelanieBusinessFactory.makeUserController();
	}
	
	private void setupSignupButton(){
		Button button = (Button)findViewById(R.id.loginSignupButton);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void setupLoginButton(){
		Button button = (Button)findViewById(R.id.loginButton);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	private void login(){
		EditText phoneTextField = (EditText)findViewById(R.id.loginPhoneNumber);
		EditText passwordTextField = (EditText)findViewById(R.id.loginPassword);
		
		if(userController != null){
			userController.checkUserExistOnCloud(getDeviceId(), new MelanieOperationCallBack<String>(){

				@Override
				public void onOperationSuccessful(String result) {
					if(result.equals(NONE)){
						Utils.makeToast(LoginActivity.this, 0); //come back to it later
					}
				}
			});
		}
	}
	
	private String getDeviceId(){
		TelephonyManager telephonyManager = (TelephonyManager)getBaseContext()
				                        .getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = telephonyManager.getDeviceId();
		return deviceId;
	}
}
