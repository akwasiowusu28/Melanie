package com.melanie.androidactivities;

import android.content.ComponentName;
import android.content.Intent;
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

import com.melanie.androidactivities.support.SingleTextListAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.CustomersController;
import com.melanie.entities.Customer;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

import java.util.ArrayList;
import java.util.List;

public class CustomersActivity extends AppCompatActivity {

    AutoCompleteTextView customerNameView;
    EditText phoneNumberView;
    private CustomersController customersController;
    private ArrayList<Customer> customers;
    private Customer customer;
    private boolean isEdit;
    private final OnItemClickListener autoCompleteListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            customer = customers.get(position);
            EditText phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
            if (customer != null) {
                phoneNumberView.setText(customer.getPhoneNumber());
            }
            isEdit = true;
            updateButtonText();
        }
    };
    private boolean wasLaunchedFromSales;
    private SingleTextListAdapter<Customer> customersAdapter;
    private Handler handler;
    private int customerId = -1;
    private boolean wasInstanceSaved = false;
    private TextWatcher textListener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (phoneNumberView != null && phoneNumberView.getText().toString().equals(LocalConstants.EMPTY_STRING)
                    && customerNameView != null
                    && customerNameView.getText().toString().equals(LocalConstants.EMPTY_STRING)) {
                isEdit = false;
                updateButtonText();
            }
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            wasInstanceSaved = true;
            customers = (ArrayList<Customer>) savedInstanceState.get(LocalConstants.CUSTOMERS);
        }
        setContentView(R.layout.activity_customers);
        initializeFields();
        setupAutoCompleteCustomers();
        setupAddCustomersListener();
    }

    private void initializeFields() {
        handler = new Handler(getMainLooper());
        customersController = BusinessFactory.makeCustomersController();
        if (!wasInstanceSaved) {
            customers = new ArrayList<Customer>();
            getAllCustomers();
        }

        customer = null;
        isEdit = false;
        wasLaunchedFromSales = wasLaunchedFromSales();
        customerNameView = (AutoCompleteTextView) findViewById(R.id.customerName);
        phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
        customerNameView.addTextChangedListener(textListener);
        phoneNumberView.addTextChangedListener(textListener);
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
            String callerClassName = callerActivity.getShortClassName().substring(1);
            String salesActivityClassName = SalesActivity.class.getSimpleName();
            value = callerClassName.equals(salesActivityClassName);
        }
        return value;
    }

    private void getAllCustomers() {
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
        customersAdapter = new SingleTextListAdapter<Customer>(this, customers);

        customerNameView.setAdapter(customersAdapter);
        customerNameView.setOnItemClickListener(autoCompleteListener);
    }

    public void saveCustomer() {
        String customerName = customerNameView.getText().toString();
        String phoneNumber = phoneNumberView.getText().toString();

        if (!Utils.isAnyFieldEmpty(phoneNumberView, customerNameView)) {
            OperationResult result = saveCustomerAndReturnResult(customerName, phoneNumber);

            if (!wasLaunchedFromSales) {
                Utils.makeToastBasedOnOperationResult(this, result, R.string.addCustomerSuccess,
                        R.string.addCustomerFailed);
            }
            Utils.clearInputTextFields(customerNameView, phoneNumberView);
            finishIfLaunchedFromSales();
            updateButtonText();
        }
    }

    private OperationResult saveCustomerAndReturnResult(String customerName, String phoneNumber) {
        OperationResult result = OperationResult.FAILED;
        try {
            if (isEdit) {
                customer.setName(customerName);
                customer.setPhoneNumber(phoneNumber);
                customerId = customer.getId();
                result = customersController.updateCustomer(customer);
            } else {
                customer = new Customer(customerName, phoneNumber);
                result = customersController.addCustomer(customer);
                customers.add(customer);
                Utils.notifyListUpdate(customersAdapter, handler);
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

            try {
                if (customerId < 0) {
                    customersController.getLastInsertedCustomerId(new OperationCallBack<Integer>() {
                        @Override
                        public void onOperationSuccessful(Integer customerId) {
                            CustomersActivity.this.customerId = customerId;
                            performFinish();
                        }
                    });
                } else {
                    performFinish();
                }
            } catch (MelanieBusinessException e) {
                e.printStackTrace(); // TODO: log it
            }
        }
    }

    private void performFinish() {
        Intent intent = getIntent();
        intent.putExtra(LocalConstants.CustomerId, customerId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {

        bundle.putSerializable(LocalConstants.CUSTOMERS, customers);
        super.onSaveInstanceState(bundle);
    }

    private static class LocalConstants {
        public static final String CUSTOMERS = "Customers";
        public static final String CustomerId = "CustomerId";
        public static final String EMPTY_STRING = "";
    }
}
