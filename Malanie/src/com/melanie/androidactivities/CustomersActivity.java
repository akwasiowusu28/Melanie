package com.melanie.androidactivities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.melanie.androidactivities.support.MelanieSingleTextListAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.CustomersController;
import com.melanie.entities.Customer;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class CustomersActivity extends Activity {

	private CustomersController customersController;
	private List<Customer> customers;
	private Customer selectedCustomer;
	private boolean isEdit;
	private boolean wasLaunchedFromSales;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customers);
		initializeFields();
		initializeCustomers();
		setupAutoCompleteCustomers();
	}

	private void initializeFields() {
		customersController = MelanieBusinessFactory.makeCustomersController();
		selectedCustomer = null;
		isEdit = false;
		wasLaunchedFromSales = wasLaunchedFromSales();
	}

	private boolean wasLaunchedFromSales() {
		String callerClassName = getCallingActivity().getShortClassName()
				.substring(1);
		String salesActivityClassName = SalesActivity.class.getSimpleName();
		return callerClassName.equals(salesActivityClassName);
	}

	private void initializeCustomers() {
		try {
			customers = customersController.getAllCustomers();
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // log it
		}
	}

	private void setupAutoCompleteCustomers() {
		MelanieSingleTextListAdapter<Customer> customersAdapter = new MelanieSingleTextListAdapter<Customer>(
				this, customers);

		AutoCompleteTextView customerNameTextView = (AutoCompleteTextView) findViewById(R.id.customerName);
		customerNameTextView.setAdapter(customersAdapter);
		customerNameTextView.setOnItemClickListener(autoCompleteListener);
	}

	private OnItemClickListener autoCompleteListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectedCustomer = customers.get(position);
			EditText phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
			if (selectedCustomer != null)
				phoneNumberView.setText(selectedCustomer.getPhoneNumber());
			isEdit = true;
			updateButtonText();
		}
	};

	public void saveCustomer(View view) {
		EditText customerNameView = (EditText) findViewById(R.id.customerName);
		EditText phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
		String customerName = customerNameView.getText().toString();
		String phoneNumber = phoneNumberView.getText().toString();

		OperationResult result = saveCustomerAndReturnResult(customerName,
				phoneNumber);

		if (!wasLaunchedFromSales)
			Utils.makeToastBasedOnOperationResult(this, result,
					R.string.addCustomerSuccess, R.string.addCustomerFailed);
		Utils.clearInputTextFields(customerNameView, phoneNumberView);
		finishIfLaunchedFromSales();
		updateButtonText();
	}

	private OperationResult saveCustomerAndReturnResult(String customerName,
			String phoneNumber) {
		OperationResult result = OperationResult.FAILED;
		try {
			if (isEdit) {
				selectedCustomer.setName(customerName);
				selectedCustomer.setPhoneNumber(phoneNumber);
				result = customersController.updateCustomer(selectedCustomer);
				isEdit = false;
			} else
				result = customersController.addNewCustomer(customerName,
						phoneNumber);
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // Log it
		}
		return result;
	}

	private void updateButtonText() {
		Button addButton = (Button) findViewById(R.id.addCustomerButton);
		int stringId = isEdit ? R.string.save : R.string.add;
		addButton.setText(getString(stringId));
	}

	private void finishIfLaunchedFromSales() {
		if (wasLaunchedFromSales) {
			Intent intent = getIntent();
			intent.putExtra(Utils.Costants.CustomerId, selectedCustomer.getId());
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
