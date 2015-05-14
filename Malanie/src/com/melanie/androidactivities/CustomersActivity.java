package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class CustomersActivity extends ActionBarActivity {

	private CustomersController customersController;
	private List<Customer> customers;
	private Customer customer;
	private boolean isEdit;
	private boolean wasLaunchedFromSales;
	private MelanieSingleTextListAdapter<Customer> customersAdapter;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customers);
		initializeFields();
		getAllCustomers();
		setupAutoCompleteCustomers();
		setupAddCustomersListener();
	}

	private void initializeFields() {
		handler = new Handler(getMainLooper());
		customersController = MelanieBusinessFactory.makeCustomersController();
		customer = null;
		isEdit = false;
		wasLaunchedFromSales = wasLaunchedFromSales();
	}

	private void setupAddCustomersListener() {
		Button saveCustomerButton = (Button) findViewById(R.id.addCustomerButton);
		saveCustomerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveCustomer();
			}
		});
	}

	private boolean wasLaunchedFromSales() {
		boolean value = false;
		ComponentName callerActivity = getCallingActivity();

		if (callerActivity != null) {
			String callerClassName = callerActivity.getShortClassName()
					.substring(1);
			String salesActivityClassName = SalesActivity.class.getSimpleName();
			value = callerClassName.equals(salesActivityClassName);
		}
		return value;
	}

	private void getAllCustomers() {
		customers = new ArrayList<Customer>();
		try {
			List<Customer> tempCustomers = null;
			tempCustomers = customersController
					.getAllCustomers(new MelanieOperationCallBack<Customer>() {

						@Override
						public void onCollectionOperationSuccessful(
								List<Customer> results) {

							Utils.mergeItems(results, customers);
							Utils.notifyListUpdate(customersAdapter, handler);
						}
					});
			if (tempCustomers != null && !tempCustomers.isEmpty())
				customers.addAll(tempCustomers);

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO: log it
		}
	}

	private void setupAutoCompleteCustomers() {
		customersAdapter = new MelanieSingleTextListAdapter<Customer>(this,
				customers);

		AutoCompleteTextView customerNameTextView = (AutoCompleteTextView) findViewById(R.id.customerName);
		customerNameTextView.setAdapter(customersAdapter);
		customerNameTextView.setOnItemClickListener(autoCompleteListener);
	}

	private OnItemClickListener autoCompleteListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			customer = customers.get(position);
			EditText phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
			if (customer != null)
				phoneNumberView.setText(customer.getPhoneNumber());
			isEdit = true;
			updateButtonText();
		}
	};

	public void saveCustomer() {
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
				customer.setName(customerName);
				customer.setPhoneNumber(phoneNumber);
			} else {
				customer = customersController.cacheTempNewCustomer(
						customerName, phoneNumber);

				if (!wasLaunchedFromSales) {
					if (isEdit)
						result = customersController.updateCustomer(customer);
					else
						result = customersController.addCachedCustomer();

					List<Customer> customersFromLocalDataStore = customersController
							.getAllCustomers(null);
					Utils.mergeItems(customersFromLocalDataStore, customers);
					Utils.notifyListUpdate(customersAdapter, handler);
				} else
					customersController.cacheCustomerInLocalDataStore(customer);
			}
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // Log it
		} finally {
			isEdit = false;
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
			try {
				intent.putExtra(Utils.Constants.CustomerId,
						customersController.getLastInsertedCustomerId());
			} catch (MelanieBusinessException e) {
				e.printStackTrace(); // TODO: log it
			}
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
