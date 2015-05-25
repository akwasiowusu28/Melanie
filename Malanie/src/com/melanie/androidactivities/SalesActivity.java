package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.support.MelanieAlertDialog;
import com.melanie.androidactivities.support.MelanieAlertDialog.MelanieAlertDialogButtonModes;
import com.melanie.androidactivities.support.PrinterType;
import com.melanie.androidactivities.support.ProductsAndSalesListViewAdapter;
import com.melanie.androidactivities.support.ReceiptPrintingHelper;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.CustomersController;
import com.melanie.business.SalesController;
import com.melanie.entities.Customer;
import com.melanie.entities.Sale;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SalesActivity extends AppCompatActivity {

	private static final int SCAN_REQUEST_CODE = 28;
	private static final int CUSTOMER_REQUEST_CODE = 288;
	private static final int PRINTER_SELECT_REQUEST_CODE = 2888;

	private Handler handler;
	private List<Sale> sales;
	private SalesController salesController;
	private CustomersController customersController;
	private ScheduledExecutorService executorService;
	private ProductsAndSalesListViewAdapter<Sale> salesListAdapter;
	private TextListener discountListener, amountListener;
	private MelanieAlertDialog alertDialog;
	private double balance, amountReceived, discount, total;
	private final boolean isPrinterFound = false;
	private ReceiptPrintingHelper receiptPrintingHelper;
	private ListView listView;
	private String printerInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales);

		initializeFields();
		setupSalesListView();
		setupButtonsListeners();
		startBarcodeScanning();
		setupTextChangedListeners();
		setupAlertDialog();
	}

	private void setupSalesListView() {

		listView = (ListView) findViewById(R.id.salesListView);
		View headerView = getLayoutInflater().inflate(
				R.layout.layout_saleitems_header, listView, false);

		listView.addHeaderView(headerView);
		listView.setAdapter(salesListAdapter);
	}

	private void initializeFields() {
		handler = new Handler(getMainLooper());
		executorService = Executors.newScheduledThreadPool(2);
		salesController = BusinessFactory.makeSalesController();
		sales = new ArrayList<Sale>();
		salesListAdapter = new ProductsAndSalesListViewAdapter<Sale>(this,
				sales, false);
		discountListener = new TextListener(R.id.discountValue);
		amountListener = new TextListener(R.id.amountReceived);
		customersController = BusinessFactory.makeCustomersController();
		amountReceived = discount = balance = total = 0;
		receiptPrintingHelper = new ReceiptPrintingHelper(this);
	}

	private void setupButtonsListeners() {
		ImageButton scanButton = (ImageButton) findViewById(R.id.scanBarcodeImgButton);
		Button cancelSaleButton = (Button) findViewById(R.id.cancelSale);
		Button saveSaleButton = (Button) findViewById(R.id.saveSale);
		scanButton.setOnClickListener(buttonsClickListener);
		cancelSaleButton.setOnClickListener(buttonsClickListener);
		saveSaleButton.setOnClickListener(buttonsClickListener);
	}

	private final OnClickListener buttonsClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.scanBarcodeImgButton:
				launchBarcodeScanner();
				break;
			case R.id.saveSale:
				saveSales();
				break;
			case R.id.cancelSale:
				clearFields();
				break;
			}
		}
	};

	private void setupAlertDialog() {
		alertDialog = makeAlertDialog();
		alertDialog.setTitle(getString(R.string.creditSaleAlertTitle));
		alertDialog.setMessage(getString(R.string.creditSaleAlertMessage));
		alertDialog.create();
	}

	private MelanieAlertDialog makeAlertDialog() {
		return new MelanieAlertDialog(this,
				MelanieAlertDialogButtonModes.YES_NO_CANCEL,
				new MelanieAlertDialog.ButtonMethods() {

					@Override
					public void yesButtonOperation() {
						startCustomerActivity();
					}

					@Override
					public void noButtonOperation() {
						saveCurrentSales(null);
					}
				});
	}

	private void startCustomerActivity() {
		Intent intent = new Intent(this, CustomersActivity.class);
		startActivityForResult(intent, CUSTOMER_REQUEST_CODE);
	}

	private void startBarcodeScanning() {
		if (!executorService.isShutdown())
			executorService.schedule(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(SalesActivity.this,
							ScanBarcodeActivity.class);
					startActivityForResult(intent, SCAN_REQUEST_CODE);
				}
			}, 100, TimeUnit.MILLISECONDS);
	}

	private void setupTextChangedListeners() {
		EditText discountText = (EditText) findViewById(R.id.discountValue);
		EditText amountText = (EditText) findViewById(R.id.amountReceived);

		discountText
				.addTextChangedListener(new TextListener(R.id.discountValue));
		amountText
				.addTextChangedListener(new TextListener(R.id.amountReceived));
	}

	public void launchBarcodeScanner() {
		executorService = Executors.newScheduledThreadPool(2);
		startBarcodeScanning();
	}

	public void saveSales() {

		recordTotals();
		if (balance < 0)
			alertDialog.show();
		else
			saveCurrentSales(null);
	}

	private void recordTotals() {
		String totalString = ((TextView) findViewById(R.id.balanceDue))
				.getText().toString();
		String discountString = ((EditText) findViewById(R.id.discountValue))
				.getText().toString();
		String amountReceivedString = ((EditText) findViewById(R.id.amountReceived))
				.getText().toString();
		String balanceString = ((TextView) findViewById(R.id.balanceDue))
				.getText().toString();

		if (!discountString.equals(Utils.Constants.EMPTY_STRING))
			discount = Double.parseDouble(discountString);

		if (!amountReceivedString.equals(Utils.Constants.EMPTY_STRING))
			amountReceived = Double.parseDouble(amountReceivedString);

		if (!balanceString.equals(Utils.Constants.EMPTY_STRING))
			balance = Double.parseDouble(balanceString);
	}

	public void clearFields() {
		// Maybe show a message for confirmation
		resetAll();
	}

	private void saveCurrentSales(Customer customer) {
		OperationResult result = OperationResult.FAILED;
		// try {
		// result = salesController.saveCurrentSales(customer, amountReceived,
		// discount, balance);
		// } catch (MelanieBusinessException e) {
		// e.printStackTrace(); // TODO log it
		// }
		printReceipt();
		// updateUIAfterSave(result);

	}

	private void printReceipt() {
		if (isPrinterFound)
			performPrint();
		else {
			Intent intent = new Intent(this, SelectPrinterActivity.class);
			intent.putExtra(Utils.Constants.PRINTER_TYPE,
					PrinterType.Receipt.toString());
			startActivityForResult(intent, PRINTER_SELECT_REQUEST_CODE);
		}

	}

	private void performPrint() {
		receiptPrintingHelper.initializePrinterWithPrinterInfo(printerInfo);
		receiptPrintingHelper.printReceipt(sales);

	}

	private void updateUIAfterSave(OperationResult result) {
		Utils.makeToastBasedOnOperationResult(this, result,
				R.string.salesSuccess, R.string.salesFailed);
		resetAll();
	}

	private void resetAll() {
		sales.clear();
		removeTextListener();
		Utils.notifyListUpdate(salesListAdapter, handler);
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
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intentData) {
		int customerId = -1;
		if (resultCode == RESULT_OK && intentData != null)
			switch (requestCode) {
			case SCAN_REQUEST_CODE:
				List<String> barcodes = intentData
						.getStringArrayListExtra(Utils.Constants.BARCODES);
				recordSalesFromBarcodes(barcodes);
				break;
			case CUSTOMER_REQUEST_CODE:
				customerId = intentData.getIntExtra(Utils.Constants.CustomerId,
						customerId);
				saveCreditSaleWithCustomer(customerId);
				break;
			case PRINTER_SELECT_REQUEST_CODE:
				printerInfo = intentData
						.getStringExtra(Utils.Constants.PRINTER_INFO);
				performPrint();
				break;
			}
	}

	private void saveCreditSaleWithCustomer(int customerId) {
		Customer customer = null;
		if (customersController != null)
			try {
				customer = customersController.findCustomer(customerId, null);
				if (customer != null)
					customer.setAmountOwed(customer.getAmountOwed() + balance);
				saveCurrentSales(customer);
			} catch (MelanieBusinessException e) {
				e.printStackTrace(); // TODO log it
			}
	}

	private void recordSalesFromBarcodes(List<String> barcodes) {

		try {
			sales.clear();
			sales.addAll(salesController.generateSaleItems(barcodes,
					new OperationCallBack<Sale>() {

						@Override
						public void onCollectionOperationSuccessful(
								List<Sale> results) {
							sales.clear();
							sales.addAll(results);
							Utils.notifyListUpdate(salesListAdapter, handler);
							updateTotalField();
						}
					}));
			Utils.notifyListUpdate(salesListAdapter, handler);
			updateTotalField();
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // log it
		}

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

	@Override
	protected void onPause() {
		super.onPause();
		if (executorService != null)
			executorService.shutdown();
	}

	private class TextListener implements TextWatcher {

		private final int senderTextViewId;

		public TextListener(int senderTextViewId) {
			this.senderTextViewId = senderTextViewId;
		}

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
			if (senderTextViewId == R.id.discountValue)
				handleDiscountChanged(s);
			else if (senderTextViewId == R.id.amountReceived)
				handleAmountReceivedChanged(s);
		}

		private void handleDiscountChanged(Editable s) {
			TextView totalTextView = (TextView) findViewById(R.id.totalValue);
			double total = Double.parseDouble(totalTextView.getText()
					.toString());
			String discountText = ((EditText) findViewById(R.id.discountValue))
					.getText().toString();
			if (discountText.equals("")) {
				updateTotalField();
				total = Double.parseDouble(totalTextView.getText().toString());
			} else if (!s.toString().equals(""))
				handleDiscountNormalCase(totalTextView, s, total);
			evaluateBalanceForDiscountChange(total);
		}

		private void handleDiscountNormalCase(TextView totalTextView,
				Editable s, double total) {
			double discount = Double.parseDouble(s.toString());
			if (!(discount > total)) {
				total -= discount;
				totalTextView.setText(String.valueOf(total));
			} else {
				s.clear();
				updateTotalField();
			}
		}

		private void evaluateBalanceForDiscountChange(double total) {
			EditText amountReceivedText = (EditText) findViewById(R.id.amountReceived);
			String amountTextValue = amountReceivedText.getText().toString();
			if (!amountTextValue.equals(""))
				calculateBalance(Double.parseDouble(amountTextValue), total);
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
			else
				Utils.resetTextFieldsToZeros(balanceTextView);
		}
	}

	@Override
	protected void onDestroy() {
		receiptPrintingHelper.clearResources();
	}
}
