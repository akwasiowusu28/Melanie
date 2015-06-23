package com.melanie.androidactivities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

import com.melanie.androidactivities.support.ProductsAndSalesListViewAdapter;
import com.melanie.androidactivities.support.SectionHeader;
import com.melanie.androidactivities.support.SingleTextListAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.CustomersController;
import com.melanie.business.SalesController;
import com.melanie.entities.Customer;
import com.melanie.entities.Payment;
import com.melanie.entities.Sale;
import com.melanie.entities.SalePayment;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private final TextWatcher amountTextChangedListener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            handleAmountReceivedChanged(s);
        }

        private void handleAmountReceivedChanged(Editable s) {
            TextView totalTextView = (TextView) findViewById(R.id.totalToPay);
            double total = Double.parseDouble(totalTextView.getText().toString());
            double amountReceived = 0;
            if (!s.toString().equals(LocalConstants.EMPTY_STRING)) {
                amountReceived = Double.parseDouble(s.toString());
            }
            calculateBalance(amountReceived, total);
        }

        private void calculateBalance(double amountReceived, double total) {
            double balance = amountReceived - total;
            TextView balanceTextView = (TextView) findViewById(R.id.paymentBalance);
            if (amountReceived != 0) {
                balanceTextView.setText(String.valueOf(balance));
            } else {
                Utils.resetTextFieldsToZeros(balanceTextView);
            }
        }
    };
    SingleTextListAdapter<Customer> customersAdapter;
    private ArrayList<Object> salesDisplay;
    private SalesController salesController;
    private CustomersController customersController;
    private ArrayList<Customer> customers;
    private Customer selectedCustomer;
    private ProductsAndSalesListViewAdapter<Object> salesListAdapter;
    private Handler handler;
    private boolean wasInstanceSaved = false;
    private ArrayList<SalePayment> salesPayments;
    private SimpleDateFormat dateformater;
    private double totalOwing = 0D;
    private Map<String, Double> balancesPerSaleDates;
    AutoCompleteTextView customerNameTextView;

    private final OnClickListener buttonsClickListener = new OnClickListener() {

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

    private final OnItemClickListener autoCompleteListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            selectedCustomer = customers.get(position);
            updateSalesBasedOnSelectedAutoComplete();

            Utils.dismissKeyboard(PaymentActivity.this, customerNameTextView);
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        if (savedInstanceState != null) {
            wasInstanceSaved = true;
            salesPayments = (ArrayList<SalePayment>) savedInstanceState.get(LocalConstants.SALES_PAYMENT);
            salesDisplay = (ArrayList<Object>) savedInstanceState.get(LocalConstants.SALES);
            customers = (ArrayList<Customer>) savedInstanceState.get(LocalConstants.CUSTOMERS);
            totalOwing = savedInstanceState.getDouble(LocalConstants.TOTAL);
            updateTotalField();
        }
        initializeFields();
        setupSalesListView();
        setupAmountTextChangedListener();
        if (!wasInstanceSaved) {
            getAllCustomers();
        }
        setupAutoCompleteCustomers();
        setupButtonsClickListener();
        Utils.dismissKeyboard(this,customerNameTextView);
    }

    private void getAllCustomers() {
        customers = new ArrayList<>();
        try {
            customers.addAll(customersController.getAllCustomers(new OperationCallBack<Customer>() {

                @Override
                public void onCollectionOperationSuccessful(List<Customer> results) {

                    Utils.mergeItems(results, customers, false);
                    Utils.notifyListUpdate(customersAdapter, handler);
                }
            }));

        } catch (MelanieBusinessException e) {
            e.printStackTrace(); // TODO: log it
        }
    }

    private void setupAutoCompleteCustomers() {
        customersAdapter = new SingleTextListAdapter<>(this, customers);


        customerNameTextView.setAdapter(customersAdapter);
        customerNameTextView.setOnItemClickListener(autoCompleteListener);
    }

    private void setupButtonsClickListener() {
        Button savePaymentButton = (Button) findViewById(R.id.savePayment);
        Button cancelPaymentButton = (Button) findViewById(R.id.cancelPayment);
        savePaymentButton.setOnClickListener(buttonsClickListener);
        cancelPaymentButton.setOnClickListener(buttonsClickListener);
    }

    private void updateSalesBasedOnSelectedAutoComplete() {
        try {
            salesController.findSalesByCustomer(selectedCustomer, new OperationCallBack<SalePayment>() {

                @Override
                public void onCollectionOperationSuccessful(List<SalePayment> results) {
                    salesPayments.clear();
                    salesPayments.addAll(results);

                    salesDisplay.clear();
                    addSales();

                    Utils.notifyListUpdate(salesListAdapter, handler);
                }
            });
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
        View headerView = getLayoutInflater().inflate(R.layout.layout_saleitems_header, listView, false);

        listView.addHeaderView(headerView);
        listView.setAdapter(salesListAdapter);
    }

    private void initializeFields() {
        handler = new Handler(getMainLooper());
        salesController = BusinessFactory.makeSalesController();
        if (!wasInstanceSaved) {
            salesDisplay = new ArrayList<>();
            salesPayments = new ArrayList<>();
        }
        customerNameTextView = (AutoCompleteTextView) findViewById(R.id.customerFind);
        dateformater = new SimpleDateFormat(LocalConstants.MMM_DD_YYYY);
        balancesPerSaleDates = new HashMap<>();
        salesListAdapter = new ProductsAndSalesListViewAdapter<>(this, salesDisplay, true);
        customersController = BusinessFactory.makeCustomersController();
    }

    private void updateTotalField() {
        TextView totalView = (TextView) findViewById(R.id.totalToPay);
        totalView.setText(String.valueOf(totalOwing));
    }

    public void savePayment() {
        String amountReceivedString = ((EditText) findViewById(R.id.paidAmount)).getText().toString();
        String balanceString = ((TextView) findViewById(R.id.paymentBalance)).getText().toString();

        double balance = 0, amountReceived = 0;

        if (!amountReceivedString.equals(LocalConstants.EMPTY_STRING)) {
            amountReceived = Double.parseDouble(amountReceivedString);
        }

        if (!balanceString.equals(LocalConstants.EMPTY_STRING)) {
            balance = Double.parseDouble(balanceString);
        }

        OperationResult result = savePayment(amountReceived, balance);

        Utils.makeToastBasedOnOperationResult(this, result, R.string.paymentSuccess, R.string.paymentFailed);
        resetAll();
    }

    private OperationResult savePayment(double amountReceived, double balance) {
        OperationResult result = OperationResult.FAILED;
        try {
            List<Sale> salesToRecord = new ArrayList<>();
            for (Object item : salesDisplay) {
                if (item instanceof Sale) {
                    Sale sale = (Sale) item;
                    salesToRecord.add(sale);
                }
            }
            result = salesController.recordPayment(selectedCustomer, salesToRecord, amountReceived, 0, balance, balancesPerSaleDates);
        } catch (MelanieBusinessException e) {
            e.printStackTrace(); // TODO log it
        }
        return result;
    }

    public void clearFields() {

        resetAll();
    }

    private void resetAll() {
        salesDisplay.clear();
        Utils.notifyListUpdate(salesListAdapter, handler);
        Utils.clearInputTextFields(findViewById(R.id.paidAmount));
        Utils.resetTextFieldsToZeros(findViewById(R.id.totalToPay), findViewById(R.id.paymentBalance));
    }

    private void addSales() {
        totalOwing = 0D;
        for (SalePayment salePayment : salesPayments) {

            Sale sale = salePayment.getSale();
            String salesDate = dateformater.format(sale.getSaleDate());
            Payment payment = salePayment.getPayment();

            boolean salesExists = balancesPerSaleDates.containsKey(salesDate);
            double balance = Math.abs(payment.getBalance());

            if (salesExists && balance < balancesPerSaleDates.get(salesDate)) {
               balancesPerSaleDates.put(salesDate, balance);
            }

            if (!salesExists) {

                double amount = totalAmountPaid(salesDate);
                balancesPerSaleDates.put(salesDate, balance);

                String sectionDisplay = salesDate + ": " + String.valueOf(amount) + " paid, "
                        + String.valueOf(balance) + " remaining";
                salesDisplay.add(new SectionHeader(sectionDisplay));

                totalOwing += balance;
            }
            if (!salesDisplay.contains(sale)) {
                salesDisplay.add(sale);
            }
        }
        updateTotalField();
    }

    private double totalAmountPaid(String date) {
        double amount = 0D;

        List<Payment> payments = new ArrayList<>();
        List<Sale> sales = new ArrayList<>();

        for (SalePayment salePayment : salesPayments) {

            Sale sale = salePayment.getSale();

            String saleDate = sale != null ?
                    dateformater.format(sale.getSaleDate()) : LocalConstants.EMPTY_STRING;
            Payment payment = salePayment.getPayment();

            if (sale != null && payment != null && saleDate.equals(date)
                    && !sales.contains(sale) && !payments.contains(payment)) {

                payments.add(payment);
                sales.add(sale);
                amount += Math.abs(payment.getAmountReceived());
            }
        }
        return amount;
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {

        bundle.putSerializable(LocalConstants.SALES_PAYMENT, salesPayments);
        bundle.putSerializable(LocalConstants.SALES, salesDisplay);
        bundle.putSerializable(LocalConstants.CUSTOMERS, customers);
        bundle.putDouble(LocalConstants.TOTAL, totalOwing);

        super.onSaveInstanceState(bundle);
    }

    private class LocalConstants {
        public static final String SALES = "sales";
        public static final String SALES_PAYMENT = "salesPayment";
        public static final String EMPTY_STRING = "";
        public static final String CUSTOMERS = "Customers";
        public static final String MMM_DD_YYYY = "MMM dd, yyyy";
        public static final String TOTAL = "total";
    }
}
