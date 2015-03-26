package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.support.SalesListViewAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.SalesController;
import com.melanie.business.concrete.SalesControllerImpl;
import com.melanie.entities.Sale;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SalesActivity extends Activity {

	private static final int SCAN_REQUEST_CODE = 28;
	private static final String BARCODE_LIST = "barcodes";
	private List<Sale> sales;
	private SalesController salesController;
	private ScheduledExecutorService executorService;
	private SalesListViewAdapter salesListAdapter;
	private TextListener discountListener;
	private TextListener amountListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales);

		initializeFields();
		setupSalesListView();
		startBarcodeScanning();
		setupTextChangedListeners();
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
		executorService = Executors.newScheduledThreadPool(2);
		salesController = new SalesControllerImpl();
		sales = new ArrayList<Sale>();
		salesListAdapter = new SalesListViewAdapter(this, sales);
		discountListener = new TextListener(R.id.discountValue);
		amountListener = new TextListener(R.id.amountReceived);

	}

	private void startBarcodeScanning() {
		if (!executorService.isShutdown()) {
			executorService.schedule(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(SalesActivity.this,
							ScanBarcodeActivity.class);
					startActivityForResult(intent, SCAN_REQUEST_CODE);
				}
			}, 100, TimeUnit.MILLISECONDS);
		}
	}

	private void setupTextChangedListeners() {
		EditText discountText = (EditText) findViewById(R.id.discountValue);
		EditText amountText = (EditText) findViewById(R.id.amountReceived);

		discountText
				.addTextChangedListener(new TextListener(R.id.discountValue));
		amountText
				.addTextChangedListener(new TextListener(R.id.amountReceived));
	}

	public void launchBarcodeScanner(View view) {
		executorService = Executors.newScheduledThreadPool(2);
		startBarcodeScanning();
	}

	public void saveSales(View view) {
		try {
			OperationResult result = salesController.saveCurrentSales();
			Utils.makeToastBasedOnOperationResult(this, result,
					R.string.salesSuccess, R.string.salesFailed);
			resetAll();
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // log it
		}
	}

	public void cancelSales(View view) {
		// Maybe show a message for confirmation
		resetAll();
	}

	private void resetAll() {
		sales.clear();
		removeTextListener();
		Utils.notifyListUpdate(salesListAdapter);
		Utils.clearInputTextFields(findViewById(R.id.amountReceived),
				findViewById(R.id.discountValue));
		Utils.resetTextFieldsToZeros(findViewById(R.id.totalValue),
				findViewById(R.id.balanceDue));
		addTextListeners();
	}

	private void addTextListeners() {
		EditText amountReceivedText = (EditText) findViewById(R.id.amountReceived);
		EditText discountText = (EditText) findViewById(R.id.discountValue);
		amountReceivedText.addTextChangedListener(amountListener);
		discountText.addTextChangedListener(discountListener);
	}

	private void removeTextListener() {
		EditText amountReceivedText = (EditText) findViewById(R.id.amountReceived);
		EditText discountText = (EditText) findViewById(R.id.discountValue);
		amountReceivedText.removeTextChangedListener(amountListener);
		discountText.removeTextChangedListener(discountListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SCAN_REQUEST_CODE && resultCode == RESULT_OK
				&& data != null) {
			List<String> barcodes = data.getStringArrayListExtra(BARCODE_LIST);
			recordSalesFromBarcodes(barcodes);
		}
	}

	private void recordSalesFromBarcodes(List<String> barcodes) {

		try {
			sales.clear();
			sales.addAll(salesController.generateSaleItems(barcodes));
			Utils.notifyListUpdate(salesListAdapter);
			updateTotalField();
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // log it
		}

	}

	private void updateTotalField() {
		double total = 0;
		for (Sale sale : sales) {
			total += (sale.getQuantitySold() * sale.getProduct().getPrice());
		}
		TextView totalView = (TextView) findViewById(R.id.totalValue);
		totalView.setText(String.valueOf(total));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (executorService != null)
			executorService.shutdown();
	}

	private class TextListener implements TextWatcher {

		private int senderTextViewId;

		public TextListener(int senderTextViewId) {
			this.senderTextViewId = senderTextViewId;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (senderTextViewId == R.id.discountValue) {
				handleDiscountChanged(s);
			} else if (senderTextViewId == R.id.amountReceived) {
				handleAmountReceivedChanged(s);
			}
		}

		private void handleDiscountChanged(Editable s) {
			if (!s.toString().equals("")) {
				TextView totalTextView = (TextView) findViewById(R.id.totalValue);
				double total = Double.parseDouble(totalTextView.getText()
						.toString());
				total -= Double.parseDouble(s.toString());
				totalTextView.setText(String.valueOf(total));
				evaluateBalanceForDiscountChange(total);
			}
		}

		private void evaluateBalanceForDiscountChange(double total){
			EditText amountReceivedText = (EditText)findViewById(R.id.amountReceived);
			String amountTextValue = amountReceivedText.getText().toString();
			if(amountTextValue != "")
				calculateBalance(Double.parseDouble(amountTextValue), total);
		}
		
		private void handleAmountReceivedChanged(Editable s) {
			if (!s.toString().equals("")) {
				TextView totalTextView = (TextView) findViewById(R.id.totalValue);
				double total = Double.parseDouble(totalTextView.getText()
						.toString());
				double amountReceived = Double.parseDouble(s.toString());
				calculateBalance(amountReceived, total);
			}
		}
		
		private void calculateBalance(double amountReceived, double total){
			double balance = amountReceived - total;
			((TextView) findViewById(R.id.balanceDue)).setText(String
					.valueOf(balance));
		}
	};

}
