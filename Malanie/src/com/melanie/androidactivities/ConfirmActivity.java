package com.melanie.androidactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.UserController;
import com.melanie.support.MelanieBusinessFactory;
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
				updateUser();
			}
		});
	}

	private void updateUser() {
        String enteredConfirmCode = ((EditText)findViewById(R.id.confirmTextField)).getText().toString();
        if(enteredConfirmCode.equals(confirmCode)){
        	try {
				userController.updateUser(phoneNumber, true);
			} catch (MelanieBusinessException e) {
				// TODO log it
				e.printStackTrace();
			}
        }
	}
}
