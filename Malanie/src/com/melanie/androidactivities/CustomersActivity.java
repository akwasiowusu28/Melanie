package com.melanie.androidactivities;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.CustomersController;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CustomersActivity extends Activity {

	private CustomersController customersController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customers);
		initializeFields();
	}

	private void initializeFields() {
		customersController = MelanieBusinessFactory.makeCustomersController();
	}

	public void addCustomer(View view) {
		EditText customerNameView = (EditText) findViewById(R.id.customerName);
		EditText phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
		OperationResult result = OperationResult.FAILED;
		try {
			result = customersController
					.addNewCustomer(customerNameView.getText().toString(),
							phoneNumberView.getText().toString());

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // Log it
		}
		Utils.makeToastBasedOnOperationResult(this, result,
				R.string.addCustomerSuccess, R.string.addCustomerFailed);
		Utils.clearInputTextFields(customerNameView,phoneNumberView);
	}
}
