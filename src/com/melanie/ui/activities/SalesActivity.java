package com.melanie.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.ui.R;
import com.melanie.ui.support.MelanieAlertDialog;
import com.melanie.ui.support.MelanieAlertDialog.ButtonModes;
import com.melanie.ui.support.PrinterType;
import com.melanie.ui.adapters.ProductsAndSalesListViewAdapter;
import com.melanie.ui.support.ReceiptPrintingHelper;
import com.melanie.ui.support.Utils;
import com.melanie.business.CustomersController;
import com.melanie.business.SalesController;
import com.melanie.entities.Customer;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SalesActivity extends AppCompatActivity {

    private Handler handler;
    private ArrayList<Sale> sales;
    private SalesController salesController;
    private CustomersController customersController;
    private ScheduledExecutorService executorService;
    private ProductsAndSalesListViewAdapter<Sale> salesListAdapter;
    private TextListener discountListener, amountListener;
    private MelanieAlertDialog alertDialog;
    private double balance = 0D, amountReceived = 0D, discount = 0D, total = 0D;
    private boolean isPrinterFound = false;
    private ReceiptPrintingHelper receiptPrintingHelper;
    private String printerInfo;
    private OperationResult saveResult;
    private boolean wasInstanceSaved = false;
    private View selectedSaleView = null;
    private int currentPosition = 0;
    private boolean isInEditProcess = false;
    private EditText discountText, amountText;

    private final OnClickListener buttonsClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scanBarcodeImgButton:
                    launchBarcodeScanner();
                    break;
                case R.id.saveSale:
                    saveSales();
                    isInEditProcess = false;
                    break;
                case R.id.cancelSale:
                    clearFields();
                    break;
            }
            Utils.dismissKeyboard(SalesActivity.this, discountText);
            Utils.dismissKeyboard(SalesActivity.this, amountText);
        }
    };
    private OnClickListener editButtonsOnclickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            TextView qtyTextView = (TextView) selectedSaleView.findViewById(R.id.qtyTextView);
            int currentValue = Integer.parseInt(qtyTextView.getText().toString());
            Sale sale = sales.get(currentPosition - 1);
            Product product = sale.getProduct();
            int quantity;
            if (product != null) {
                quantity = product.getQuantity();

                switch (v.getId()) {
                    case R.id.increaseButton:

                        if (quantity - 1 >= 0) {
                            currentValue++;
                            product.setQuantity(quantity - 1);
                            qtyTextView.setText(String.valueOf(currentValue));
                        }
                        break;
                    case R.id.decreaseButton:
                        if (currentValue > 1) {
                            currentValue--;
                            product.setQuantity(quantity + 1);
                            qtyTextView.setText(String.valueOf(currentValue));
                        }
                        break;
                    case R.id.saveSaleQtyButton:
                        sale.setQuantitySold(currentValue);
                        Utils.notifyListUpdate(salesListAdapter, handler);
                        updateTotalField();
                        configureViewLayout(false);
                        break;
                }
            }
        }
    };

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        if (savedInstanceState != null) {
            wasInstanceSaved = true;
            isInEditProcess = savedInstanceState.getBoolean(LocalConstants.IS_IN_EDIT_PROCESS);
            sales = (ArrayList<Sale>) savedInstanceState.get(LocalConstants.SALES);
        }

        initializeFields();
        setupSalesListView();
        setupButtonsListeners();
        if (!wasInstanceSaved) {
            startBarcodeScanning();
        }
        setupTextChangedListeners();
        setupAlertDialog();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sales_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        currentPosition = menuInfo.position;

        switch (item.getItemId()) {

            case R.id.editSaleQtyMenuItem:
                if (!isInEditProcess) {
                    editSale();
                }
                break;
            case R.id.deleteSaleMenuItem:
                deleteSale();
                break;
        }

        return true;
    }

    private void setupSalesListView() {

        ListView listView = (ListView) findViewById(R.id.salesListView);
        View headerView = getLayoutInflater().inflate(R.layout.layout_saleitems_header, listView, false);

        listView.addHeaderView(headerView);
        listView.setAdapter(salesListAdapter);
        registerForContextMenu(listView);
    }

    private void initializeFields() {
        handler = new Handler(getMainLooper());
        executorService = Executors.newScheduledThreadPool(2);
        salesController = BusinessFactory.makeSalesController();
        if (!wasInstanceSaved) {
            sales = new ArrayList<>();
        }
        discountText = (EditText) findViewById(R.id.discountValue);
        amountText = (EditText) findViewById(R.id.amountReceived);
        salesListAdapter = new ProductsAndSalesListViewAdapter<>(this, sales, false);
        discountListener = new TextListener(R.id.discountValue);
        amountListener = new TextListener(R.id.amountReceived);
        customersController = BusinessFactory.makeCustomersController();
        amountReceived = discount = balance = 0;
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

    private void setupAlertDialog() {
        alertDialog = makeAlertDialog();
        alertDialog.setTitle(getString(R.string.creditSaleAlertTitle));
        alertDialog.setMessage(getString(R.string.creditSaleAlertMessage));
        alertDialog.create();
    }

    private MelanieAlertDialog makeAlertDialog() {
        return new MelanieAlertDialog(this, ButtonModes.YES_NO_CANCEL, new MelanieAlertDialog.ButtonMethods() {

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
        startActivityForResult(intent, LocalConstants.CUSTOMER_REQUEST_CODE);
    }

    private void startBarcodeScanning() {
        if (!executorService.isShutdown()) {
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SalesActivity.this, ScanBarcodeActivity.class);
                    startActivityForResult(intent, LocalConstants.SCAN_REQUEST_CODE);
                }
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    private void setupTextChangedListeners() {


        discountText.addTextChangedListener(new TextListener(R.id.discountValue));
        amountText.addTextChangedListener(new TextListener(R.id.amountReceived));
    }

    public void launchBarcodeScanner() {
        executorService = Executors.newScheduledThreadPool(2);
        startBarcodeScanning();
    }

    public void saveSales() {

        recordTotals();
        if (amountReceived < total) {
            alertDialog.show();
        } else {
            saveCurrentSales(null);
        }
    }

    private void recordTotals() {
        String discountString = ((EditText) findViewById(R.id.discountValue)).getText().toString();
        String amountReceivedString = ((EditText) findViewById(R.id.amountReceived)).getText().toString();
        String balanceString = ((TextView) findViewById(R.id.balanceDue)).getText().toString();

        if (!discountString.equals(LocalConstants.EMPTY_STRING)) {
            discount = Double.parseDouble(discountString);
        }

        if (!amountReceivedString.equals(LocalConstants.EMPTY_STRING)) {
            amountReceived = Double.parseDouble(amountReceivedString);
        }

        if (!balanceString.equals(LocalConstants.EMPTY_STRING)) {
            balance = Double.parseDouble(balanceString);
        }
    }

    public void clearFields() {
        // Maybe show a message for confirmation
        resetAll();
    }

    private void saveCurrentSales(Customer customer) {
        try {
            saveResult = salesController.saveSales(new ArrayList<>(sales), customer, amountReceived, discount, balance);
        } catch (MelanieBusinessException e) {
            e.printStackTrace(); // TODO log it
        }
        if (!Utils.Constants.PRINTING_OFF) {
            printReceipt();
        }

        updateUIAfterSave(saveResult);
    }

    private void printReceipt() {
        if (isPrinterFound) {
            performPrint();
        } else {
            Intent intent = new Intent(this, SelectPrinterActivity.class);
            intent.putExtra(LocalConstants.PRINTER_TYPE, PrinterType.Receipt.toString());
            startActivityForResult(intent, LocalConstants.PRINTER_SELECT_REQUEST_CODE);
        }

    }

    private void performPrint() {
        ArrayList<Sale> salesToPrintCache = new ArrayList<>(sales);
        receiptPrintingHelper.initializePrinterWithPrinterInfo(printerInfo);
        receiptPrintingHelper.printReceipt(salesToPrintCache);
    }

    private void updateUIAfterSave(OperationResult result) {
        Utils.makeToastBasedOnOperationResult(this, result, R.string.salesSuccess, R.string.salesFailed);
        resetAll();
    }

    private void resetAll() {
        sales.clear();
        salesController.clear();
        removeTextListener();
        Utils.notifyListUpdate(salesListAdapter, handler);
        Utils.clearInputTextFields(findViewById(R.id.amountReceived), findViewById(R.id.discountValue));
        Utils.resetTextFieldsToZeros(findViewById(R.id.totalValue), findViewById(R.id.balanceDue));
        addTextListeners();
        saveResult = OperationResult.FAILED;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        int customerId = -1;
        if (resultCode == RESULT_OK && intentData != null) {
            switch (requestCode) {
                case LocalConstants.SCAN_REQUEST_CODE:
                    List<String> barcodes = intentData.getStringArrayListExtra(LocalConstants.BARCODES);
                    recordSalesFromBarcodes(barcodes);
                    break;
                case LocalConstants.CUSTOMER_REQUEST_CODE:
                    customerId = intentData.getIntExtra(LocalConstants.CustomerId, customerId);
                    saveCreditSaleWithCustomer(customerId);
                    break;
                case LocalConstants.PRINTER_SELECT_REQUEST_CODE:
                    printerInfo = intentData.getStringExtra(LocalConstants.PRINTER_INFO);
                    isPrinterFound = true;
                    printReceipt();
                    updateUIAfterSave(saveResult);
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case LocalConstants.PRINTER_SELECT_REQUEST_CODE:
                    updateUIAfterSave(saveResult);
                    break;
            }
        }
    }

    private void saveCreditSaleWithCustomer(int customerId) {
        Customer customer;
        if (customersController != null) {
            try {
                customer = customersController.findCustomer(customerId, null);
                if (customer != null) {
                    customer.setAmountOwed(customer.getAmountOwed() + Math.abs(balance));
                }
                saveCurrentSales(customer);
            } catch (MelanieBusinessException e) {
                e.printStackTrace(); // TODO log it
            }
        }
    }

    private void recordSalesFromBarcodes(List<String> barcodes) {

        try {
            if (!barcodes.isEmpty()) {
                sales.clear();
                sales.addAll(salesController.generateSaleItems(barcodes, new OperationCallBack<Sale>() {

                    @Override
                    public void onCollectionOperationSuccessful(List<Sale> results) {
                        sales.clear();
                        sales.addAll(results);
                        Utils.notifyListUpdate(salesListAdapter, handler);
                        updateTotalField();
                    }
                }));
                Utils.notifyListUpdate(salesListAdapter, handler);
            }
            updateTotalField();
        } catch (MelanieBusinessException e) {
            e.printStackTrace(); // log it
        }

    }

    private void updateTotalField() {
        if (!sales.isEmpty()) {
            total = 0D;
            for (Sale sale : sales) {
                total += sale.getQuantitySold() * sale.getProduct().getPrice();
            }
            TextView totalView = (TextView) findViewById(R.id.totalValue);
            totalView.setText(String.valueOf(total));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {

        bundle.putSerializable(LocalConstants.SALES, sales);
        bundle.putBoolean(LocalConstants.IS_IN_EDIT_PROCESS, isInEditProcess);
        super.onSaveInstanceState(bundle);
    }

    private void editSale() {
        selectedSaleView = salesListAdapter.getLongClickedView();
        ImageButton increaseButton = (ImageButton) selectedSaleView.findViewById(R.id.increaseButton);
        ImageButton decreaseButton = (ImageButton) selectedSaleView.findViewById(R.id.decreaseButton);
        Button saveEditButton = (Button) selectedSaleView.findViewById(R.id.saveSaleQtyButton);

        decreaseButton.setOnClickListener(editButtonsOnclickListener);
        increaseButton.setOnClickListener(editButtonsOnclickListener);
        saveEditButton.setOnClickListener(editButtonsOnclickListener);

        configureViewLayout(true);
    }

    private void configureViewLayout(boolean isInEditMode) {
        ImageButton increaseButton = (ImageButton) selectedSaleView.findViewById(R.id.increaseButton);
        ImageButton decreaseButton = (ImageButton) selectedSaleView.findViewById(R.id.decreaseButton);
        Button saveEditButton = (Button) selectedSaleView.findViewById(R.id.saveSaleQtyButton);
        TextView qtyTextView = (TextView) selectedSaleView.findViewById(R.id.qtyTextView);
        TextView totalTextView = (TextView) selectedSaleView.findViewById(R.id.totalTextView);

        if (isInEditMode) {
            Utils.switchViewVisibitlity(true, increaseButton, decreaseButton, saveEditButton);
            Utils.switchViewVisibitlity(false, totalTextView);
            qtyTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            selectedSaleView.setEnabled(false);
        } else {
            Utils.switchViewVisibitlity(false, increaseButton, decreaseButton, saveEditButton);
            Utils.switchViewVisibitlity(true, totalTextView);
            qtyTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            selectedSaleView.setEnabled(true);
        }
    }

    private void deleteSale() {
        sales.remove(currentPosition - 1);
        salesController.removeFromTempList(currentPosition - 1);
        Utils.notifyListUpdate(salesListAdapter, handler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiptPrintingHelper.clearResources();
    }

    private class LocalConstants {
        public static final String SALES = "sales";
        public static final int SCAN_REQUEST_CODE = 28;
        public static final int CUSTOMER_REQUEST_CODE = 288;
        public static final int PRINTER_SELECT_REQUEST_CODE = 2888;
        public static final String IS_IN_EDIT_PROCESS = "isEditInProgress";
        public static final String PRINTER_INFO = "printerInfo";
        public static final String PRINTER_TYPE = "printerType";
        public static final String CustomerId = "CustomerId";
        public static final String BARCODES = "barcodes";
        public static final String EMPTY_STRING = "";
    }

    private class TextListener implements TextWatcher {

        private final int senderTextViewId;

        public TextListener(int senderTextViewId) {
            this.senderTextViewId = senderTextViewId;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
            TextView totalTextView = (TextView) findViewById(R.id.totalValue);

            String discountText = ((EditText) findViewById(R.id.discountValue)).getText().toString();
            if (discountText.equals(LocalConstants.EMPTY_STRING)) {
                updateTotalField();
                total = Double.parseDouble(totalTextView.getText().toString());
            } else if (!s.toString().equals(LocalConstants.EMPTY_STRING)) {
                handleDiscountNormalCase(totalTextView, s, total);
            }
            evaluateBalanceForDiscountChange(total);
        }

        private void handleDiscountNormalCase(TextView totalTextView, Editable s, double total) {
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
            if (!amountTextValue.equals(LocalConstants.EMPTY_STRING)) {
                calculateBalance(Double.parseDouble(amountTextValue), total);
            }
        }

        private void handleAmountReceivedChanged(Editable s) {
            TextView totalTextView = (TextView) findViewById(R.id.totalValue);
            double total = Double.parseDouble(totalTextView.getText().toString());
            double amountReceived = 0;
            if (!s.toString().equals(LocalConstants.EMPTY_STRING)) {
                amountReceived = Double.parseDouble(s.toString());
            }
            calculateBalance(amountReceived, total);
        }

        private void calculateBalance(double amountReceived, double total) {
            double balance = amountReceived - total;
            TextView balanceTextView = (TextView) findViewById(R.id.balanceDue);
            if (amountReceived != 0) {
                balanceTextView.setText(String.valueOf(balance));
            } else {
                Utils.resetTextFieldsToZeros(balanceTextView);
            }
        }
    }
}
