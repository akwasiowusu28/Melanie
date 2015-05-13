package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.support.MelanieSingleTextListAdapter;
import com.melanie.androidactivities.support.ProductsAndSalesListViewAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.CustomersController;
import com.melanie.business.SalesController;
import com.melanie.entities.Customer;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class PaymentActivity extends ActionBarActivity {

	private List<Sale> sales;
	private SalesController salesController;
	private CustomersController customersController;
	private List<Customer> customers;
	private Customer selectedCustomer;
	private ProductsAndSalesListViewAdapter<Sale> salesListAdapter;
	private Handler handler;
	MelanieSingleTextListAdapter<Customer> customersAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		initializeFields();
		setupSalesListView();
		setupAmountTextChangedListener();
		getAllCustomers();
		setupAutoCompleteCustomers();
		setupButtonsClickListener();
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

		AutoCompleteTextView customerNameTextView = (AutoCompleteTextView) findViewById(R.id.customerFind);
		customerNameTextView.setAdapter(customersAdapter);
		customerNameTextView.setOnItemClickListener(autoCompleteListener);
	}

	private void setupButtonsClickListener() {
		Button savePaymentButton = (Button) findViewById(R.id.savePayment);
		Button cancelPaymentButton = (Button) findViewById(R.id.cancelPayment);
		savePaymentButton.setOnClickListener(buttonsClickListener);
		cancelPaymentButton.setOnClickListener(buttonsClickListener);
	}

	private OnClickListener buttonsClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.savePayment:
				savePayment();
				break;
			case R.id.cancelPayment:
				clearFields();
				break;
			default:
				break;
			}
		}
	};

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
			sales.clear();
			List<Sale> tempSales = salesController.findSalesByCustomer(
					selectedCustomer, new MelanieOperationCallBack<Sale>() {

						@Override
						public void onCollectionOperationSuccessful(
								List<Sale> results) {
							Utils.mergeItems(results, sales);
							Utils.notifyListUpdate(salesListAdapter, handler);
						}
					});
			sales.addAll(tempSales);
			Utils.notifyListUpdate(salesListAdapter, handler);
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // log it
		}
	}

	private void setupAmountTextChangedListener() {
		EditText amountReceivedText = (EditText) findViewById(R.id.paidAmount);
		amountReceivedText.addTextChangedListener(amountTextChangedListener);
	}

	private void setupSalesListView() {

		ListView listView = (ListView) findViewById(R.id.salesListView);
		View headerView = getLayoutInflater().inflate(
				R.layout.layout_saleitems_header, listView, false);

		listView.addHeaderView(headerView);
		listView.setAdapter(salesListAdapter);
	}

	private void initializeFields() {
		handler = new Handler(getMainLooper());
		salesController = MelanieBusinessFactory.makeSalesController();
		sales = new ArrayList<Sale>();
		salesListAdapter = new ProductsAndSalesListViewAdapter<Sale>(this,
				sales, false);
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

	public void savePayment() {
		String amountReceivedString = ((EditText) findViewById(R.id.amountReceived))
				.getText().toString();
		String balanceString = ((TextView) findViewById(R.id.balanceDue))
				.getText().toString();

		double balance = 0, amountReceived = 0;

		if (!amountReceivedString.equals(Utils.Costants.EMPTY_STRING))
			amountReceived = Double.parseDouble(amountReceivedString);

		if (!balanceString.equals(Utils.Costants.EMPTY_STRING))
			balance = Double.parseDouble(balanceString);

		OperationResult result = savePayment(amountReceived, balance);

		Utils.makeToastBasedOnOperationResult(this, result,
				R.string.paymentSuccess, R.string.paymentFailed);
		resetAll();
	}

	private OperationResult savePayment(double amountReceived, double balance) {
		OperationResult result = OperationResult.FAILED;
		try {
			result = salesController.recordPayment(selectedCustomer, sales,
					amountReceived, 0, balance);
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO log it
		}
		return result;
	}

	public void clearFields() {

		resetAll();
	}

	private void resetAll() {
		sales.clear();
		Utils.notifyListUpdate(salesListAdapter, handler);
		Utils.clearInputTextFields(findViewById(R.id.paidAmount));
		Utils.resetTextFieldsToZeros(findViewById(R.id.totalToPay),
				findViewById(R.id.paymentBalance));
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
