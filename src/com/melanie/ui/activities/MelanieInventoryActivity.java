package com.melanie.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.melanie.ui.R;
import com.melanie.ui.support.BarcodePrintHelper;
import com.melanie.ui.support.FileShareHelper;
import com.melanie.ui.adapters.ProductsAndSalesListViewAdapter;
import com.melanie.ui.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MelanieInventoryActivity extends AppCompatActivity {

    FileShareHelper fileShareHelper;
    private ArrayList<Product> allProducts;
    private ProductsAndSalesListViewAdapter<Product> productsAdapter;
    private Handler handler;
    private ProductEntryController productController;
    private ArrayList<Product> currentProducts;

    private ArrayAdapter<Category> categoriesAdapter;
    private ArrayList<Category> categories;
    private boolean instanceWasSaved = false;
    private int selectedCategoryPosition = 0;
    private boolean isInEditProcess;


    private String currentBarcode = null;
    private BarcodePrintHelper barcodePrintHelper;
    private View selectedListViewChild;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melanie_inventory);

        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (savedInstanceState != null) {
            instanceWasSaved = true;
            isInEditProcess = savedInstanceState.getBoolean(LocalConstants.IS_IN_EDIT_PROCESS);
            restoreInstanceState(savedInstanceState);
        }
        initializeFields();

        setupCategoriesSpinner();
        setupProductsListView();
        setupSwipeRefresh();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_menu, menu);
        if (Utils.Constants.PRINTING_OFF) {
            menu.getItem(1).setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

            case R.id.editMenuItem:
                editProduct(menuInfo.position);
                break;
            case R.id.printBarcodeMenuItem:
                printBarcode(menuInfo.position);
                break;
            case R.id.sharebarcodeMenuItem:
                shareBarcodeImage(menuInfo.position);
        }

        return true;
    }

    private void editProduct(int position) {
        if (!isInEditProcess) {

            selectedListViewChild = productsAdapter.getLongClickedView();

            switchSelectedListItemVisibility(true);
            View editView = selectedListViewChild.findViewById(R.id.editProductView);
            setupEditView(editView, position);
            isInEditProcess = true;
        }
    }

    private void printBarcode(int position) {
        if (barcodePrintHelper == null) { //lazy init
            barcodePrintHelper = new BarcodePrintHelper(this, false);
        }
        Product product = currentProducts.get(position - 1); // This is because the listview header takes position 0 so the position coming in is off by one */
        currentBarcode = product.getBarcode();
        barcodePrintHelper.printBarcode(currentBarcode, 1, null);
    }

    private void shareBarcodeImage(int position) {

        if (fileShareHelper != null) {
            Product product = currentProducts.get(position - 1); // This is because the listview header takes position 0 so the position coming in is off by one */

            String productName = product.getProductName();
            String productBarcode = product.getBarcode();

            fileShareHelper.shareBarcodeImage(productBarcode, productName);
        }
    }

    private void switchSelectedListItemVisibility(boolean showEditView) {

        if (selectedListViewChild != null) {

            View mainView = selectedListViewChild.findViewById(R.id.mainDisplayRow);
            View editView = selectedListViewChild.findViewById(R.id.editProductView);

            Utils.switchViewVisibitlity(showEditView, editView);
            Utils.switchViewVisibitlity(!showEditView, mainView);
        }
    }

    private void initializeFields() {
        handler = new Handler(getMainLooper());
        productController = BusinessFactory.makeProductEntryController();
        if (!instanceWasSaved) {
            categories = new ArrayList<>();
            categories.add(0, new Category(getString(R.string.allCategories)));
            allProducts = new ArrayList<>();
            currentProducts = new ArrayList<>();
            getAllCategories();
            getAllProducts();
        }

        fileShareHelper = new FileShareHelper(this);
        categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        productsAdapter = new ProductsAndSalesListViewAdapter<>(this, currentProducts, false);
    }

    private void setupEditView(View editView, final int position) {
        final Product product = currentProducts.get(position - 1); // This is because the listview header takes position 0 so the position coming in is off by one */

        Button button = (Button) editView.findViewById(R.id.productEditButton);

        final EditText newProductNameView = (EditText) editView.findViewById(R.id.productEditNameView);
        newProductNameView.setText(product.getProductName());

        final EditText newQuantityView = (EditText) editView.findViewById(R.id.productEditQuatityView);
        newQuantityView.setText(String.valueOf(product.getQuantity()));

        final EditText newPriceView = (EditText) editView.findViewById(R.id.productEditPriceView);
        newPriceView.setText(String.valueOf(product.getPrice()));

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                product.setProductName(newProductNameView.getText().toString());
                product.setPrice(Double.parseDouble(newPriceView.getText().toString()));
                product.setQuantity(Integer.parseInt(newQuantityView.getText().toString()));
                updateProduct(product);

                Utils.makeToast(MelanieInventoryActivity.this, R.string.productUpdateSucessfull);
                switchSelectedListItemVisibility(false);
                Utils.notifyListUpdate(productsAdapter, handler);
                isInEditProcess = false;
            }
        });
    }

    private void updateProduct(Product product) {
        try {
            productController.updateProduct(product);
        } catch (MelanieBusinessException e) {
            // TODO log it
            e.printStackTrace();
        }
    }

    private void setupCategoriesSpinner() {
        Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
        categorySpinner.setAdapter(categoriesAdapter);
        categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryPosition = position;
                setCurrentProducts(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setCurrentProducts(int categoryPosition) {

        currentProducts.clear();

        if (categoryPosition == 0) {
            currentProducts.addAll(allProducts);
        } else {
            int categoryId = categories.get(categoryPosition).getId();
            for (Product product : allProducts) {
                Category productCategory = product.getCategory();
                if (productCategory != null && productCategory.getId() == categoryId) {
                    currentProducts.add(product);
                }
            }
        }
        Utils.notifyListUpdate(productsAdapter, handler);
    }

    private void setupProductsListView() {
        ListView listView;
        listView = (ListView) findViewById(R.id.productsListView);
        View headerView = getLayoutInflater().inflate(R.layout.layout_saleitems_header, listView, false);

        listView.addHeaderView(headerView);
        listView.setAdapter(productsAdapter);
        Utils.notifyListUpdate(productsAdapter, handler);

        registerForContextMenu(listView);
    }

    private void setupSwipeRefresh(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshList);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getAllProducts();
        }
    };

    private void getAllCategories() {
        try {
            categories.addAll(productController.getAllCategories(new OperationCallBack<Category>() {
                @Override
                public void onCollectionOperationSuccessful(List<Category> results) {
                    Utils.mergeItems(results, categories, true);
                    Utils.notifyListUpdate(categoriesAdapter, handler);
                }
            }));
            Utils.notifyListUpdate(categoriesAdapter, handler);
        } catch (MelanieBusinessException e) {
            e.printStackTrace(); // TODO: log it
        }
    }

    private void getAllProducts() {
        if (productController != null) {
            try {
                if(!allProducts.isEmpty()){
                    allProducts.clear();
                }
                allProducts.addAll(productController.findAllProducts(new OperationCallBack<Product>() {
                    @Override
                    public void onCollectionOperationSuccessful(List<Product> results) {
                        Utils.mergeItems(results, allProducts, false);
                        setCurrentProducts(selectedCategoryPosition);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }));
                Utils.notifyListUpdate(productsAdapter, handler);
            } catch (MelanieBusinessException e) {
                e.printStackTrace(); // TODO log it
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        if (resultCode == RESULT_OK && requestCode == Utils.Constants.PRINTER_SELECT_REQUEST_CODE) {

            Bundle bundle = intentData.getExtras();
            Map<String, String> printerInfo = (Map<String, String>) bundle.get(LocalConstants.PRINTER_INFO);
            if (printerInfo != null) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                if (barcodePrintHelper != null) {
                    barcodePrintHelper.setIsPrinterFound(true);
                    barcodePrintHelper.printBarcode(currentBarcode, 1, printerInfo);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable(LocalConstants.ALLPRODUCTS, allProducts);
        bundle.putSerializable(LocalConstants.CATEGORIES, categories);
        bundle.putSerializable(LocalConstants.CURRENTPRODUCTS, currentProducts);
        super.onSaveInstanceState(bundle);
    }

    @SuppressWarnings("unchecked")
    private void restoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            bundle.putBoolean(LocalConstants.IS_IN_EDIT_PROCESS, isInEditProcess);
            allProducts = (ArrayList<Product>) bundle.get(LocalConstants.ALLPRODUCTS);
            categories = (ArrayList<Category>) bundle.get(LocalConstants.CATEGORIES);
            currentProducts = (ArrayList<Product>) bundle.get(LocalConstants.CURRENTPRODUCTS);
        }
    }

    @Override
    protected void onDestroy() {
        if (barcodePrintHelper != null) {
            barcodePrintHelper.clearResources();
            barcodePrintHelper = null;
        }
        if (fileShareHelper != null) {
            fileShareHelper.performCleanup();
            fileShareHelper = null;
        }
        super.onDestroy();
    }

    private class LocalConstants {
        public static final String CATEGORIES = "categories";
        public static final String CURRENTPRODUCTS = "currentProducts";
        public static final String ALLPRODUCTS = "allProducts";
        public static final String IS_IN_EDIT_PROCESS = "isEditInProgress";
        public static final String PRINTER_INFO = "printerInfo";
    }
}
