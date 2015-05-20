package com.melanie.androidactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.MelanieBusiness;
import com.melanie.business.UserController;
import com.melanie.business.concrete.MelanieBusinessImpl;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class ConfirmActivity extends ActionBarActivity {

	private Button confirmButton;
	private String phoneNumber;
	private String confirmCode;
	private UserController userController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm);
		initializeFields();
		setupConfirmButton();
	}

	private void initializeFields() {
		userController = MelanieBusinessFactory.makeUserController();
		Intent intent = getIntent();

		phoneNumber = intent.getStringExtra(Utils.Constants.PHONE_NUMBER);
		confirmCode = intent.getStringExtra(Utils.Constants.CONFIRM_CODE);
	}

	private void setupConfirmButton() {
		confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(updateUser().equals(OperationResult.SUCCESSFUL)){
					launchMainActivity();
				}
			}
		});
	}

	private OperationResult updateUser() {
		OperationResult result = OperationResult.FAILED;
        String enteredConfirmCode = ((EditText)findViewById(R.id.confirmTextField)).getText().toString();
        if(enteredConfirmCode.equals(confirmCode)){
        	try {
				userController.updateUser(phoneNumber, true);
				result = OperationResult.SUCCESSFUL;
			} catch (MelanieBusinessException e) {
				// TODO log it
				e.printStackTrace();
			}
        }
        return result;
	}
	
	private void launchMainActivity(){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MelanieBusinessImpl.clearResources();
	}
}
