package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.support.MelanieSingleTextListAdapter;
import com.melanie.androidactivities.support.SalesListViewAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.CustomersController;
import com.melanie.business.SalesController;
import com.melanie.entities.Customer;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.exceptions.MelanieBusinessException;

public class PaymentActivity extends Activity {

	private List<Sale> sales;
	private SalesController salesController;
	private CustomersController customersController;
	private List<Customer> customers;
	private Customer selectedCustomer;
	private SalesListViewAdapter salesListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		initializeFields();
		setupSalesListView();
		setupAmountTextChangedListener();
		initializeCustomers();
		setupAutoCompleteCustomers();
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
			updateSalesBasedOnSelectedAutoComplete();
		}
	};

	private void updateSalesBasedOnSelectedAutoComplete() {
		try {
			sales = salesController.findSalesByCustomer(selectedCustomer);
			Utils.notifyListUpdate(salesListAdapter);
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // log it
		}
	}

	private void setupAmountTextChangedListener() {
		EditText amountReceivedText = (EditText) findViewById(R.id.amountReceived);
		amountReceivedText.addTextChangedListener(amountTextChangedListener);
	}

	private void setupSalesListView() {

		ListView listView = (ListView) findViewById(R.id.salesListView);
		View headerView = getLayoutInflater().inflate(
				R.layout.layout_saleitems_header, listView, false);
		View emptyView = findViewById(R.id.emptyView);

		listView.setEmptyView(emptyView);

		listView.addHeaderView(headerView);
		listView.setAdapter(salesListAdapter);
	}

	private void initializeFields() {
		salesController = MelanieBusinessFactory.makeSalesController();
		sales = new ArrayList<Sale>();
		salesListAdapter = new SalesListViewAdapter(this, sales);
		customersController = MelanieBusinessFactory.makeCustomersController();
	}

	private void updateTotalField() {
		if (!sales.isEmpty()) {
			double total = 0;
			for (Sale sale : sales)
				total += (sale.getQuantitySold() * sale.getProduct().getPrice());
			TextView totalView = (TextView) findViewById(R.id.totalValue);
			totalView.setText(String.valueOf(total));
		}
	}

	private TextWatcher amountTextChangedListener = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			handleAmountReceivedChanged(s);
		}

		private void handleAmountReceivedChanged(Editable s) {
			TextView totalTextView = (TextView) findViewById(R.id.totalValue);
			double total = Double.parseDouble(totalTextView.getText()
					.toString());
			double amountReceived = 0;
			if (!s.toString().equals(""))
				amountReceived = Double.parseDouble(s.toString());
			calculateBalance(amountReceived, total);
		}

		private void calculateBalance(double amountReceived, double total) {
			double balance = amountReceived - total;
			TextView balanceTextView = (TextView) findViewById(R.id.balanceDue);
			if (amountReceived != 0)
				balanceTextView.setText(String.valueOf(balance));
			else {
				Utils.resetTextFieldsToZeros(balanceTextView);
				updateTotalField();
			}
		}
	};
}
