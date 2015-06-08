package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.melanie.androidactivities.support.BarcodePrintHelper;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.support.BusinessFactory;
import com.melanie.support.CodeStrings;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

@SuppressWarnings("unchecked")
public class AddProductActivity extends AppCompatActivity {

	private class LocalConstants{

	}

	private ProductEntryController productController;

	private Map<String, String> printerInfo = null;

	private String currentBarcode = null;
	private int currentProductQuantity = 1;

	private boolean bluetoothEnableRefused = false;

	private static boolean bluetoothRequestMade;

	private Handler handler;

	private ArrayAdapter<Category> categoriesAdapter;
	private ArrayList<Category> categories;

	private boolean instanceWasSaved = false;

	private BarcodePrintHelper barcodePrintHelper;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);

		if (savedInstanceState != null) {
			restoreInstanceState(savedInstanceState);
			instanceWasSaved = true;
		}

		initializeFields();

		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		categorySpinner.setAdapter(categoriesAdapter);


		if(!bluetoothRequestMade) {
			bluetoothRequestMade = true;
			barcodePrintHelper = new BarcodePrintHelper(this, bluetoothEnableRefused);
		}
		setupAddProductListener();

	}

	private void initializeFields() {
		productController = BusinessFactory.makeProductEntryController();
		handler = new Handler(getMainLooper());
		if(!instanceWasSaved) {
			getAllCategories();
		}
		categoriesAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, categories);
	}

	private void getAllCategories() {
		categories = new ArrayList<Category>();
		try {
			List<Category> tempCategories = null;
			tempCategories = productController.getAllCategories(new OperationCallBack<Category>() {
				@Override
				public void onCollectionOperationSuccessful(List<Category> results) {
					Utils.mergeItems(results, categories, false);
					Utils.notifyListUpdate(categoriesAdapter, handler);
				}
			});
			if (tempCategories != null && !tempCategories.isEmpty()) {
				categories.addAll(tempCategories);
			}

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO: log it
		}
	}

	private void setupAddProductListener() {
		Button addProductButton = (Button) findViewById(R.id.addProductButton);
		addProductButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addProduct();
			}
		});
	}

	/**
	 * Add a new product from the user input
	 * 
	 * @param view
	 *            the add button
	 */
	public void addProduct() {

		Category category = getSelectedCategory();

		EditText productNameField = (EditText) findViewById(R.id.productName);
		EditText priceField = (EditText) findViewById(R.id.price);
		EditText quantityField = (EditText) findViewById(R.id.quantity);

		if(!isAnyFieldInvalid(priceField,productNameField, quantityField)){

			String productName = productNameField.getText().toString();
			String priceStr = priceField.getText().toString();
			String quantity = quantityField.getText().toString();

			double price = Double.parseDouble(priceStr);
			currentProductQuantity = Integer.parseInt(quantity);
			addProduct(category, productName, price);
		}
	}

	private boolean isAnyFieldInvalid(EditText... fields){
		boolean isAnyInValid = false;
		for(EditText field: fields)
		{
			if(field != null && field.getText().toString().equals(CodeStrings.EMPTY_STRING)) {
				Utils.switchInvalidFieldsBackColor(false, field);
				if(!isAnyInValid) {
					isAnyInValid = true;
				}
			} else {
				Utils.switchInvalidFieldsBackColor(true, field);
			}
		}
		return isAnyInValid;
	}

	private void addProduct(final Category category,
			final String productName, final double price) {

		try {
			productController.getLastInsertedProductId(new OperationCallBack<Integer>(){

				@Override
				public void onOperationSuccessful(Integer result) {

					performAddProduct(category, productName, price, result);
				}
			});
		} catch (MelanieBusinessException e) {
			Utils.makeToast(this, R.string.productAddFailed);
			// TODO Log it
			e.printStackTrace();
		}
	}

	private void performAddProduct( Category category,
			String productName,  double price, int lastProductId){

		currentBarcode = generateBarcodeString(lastProductId);
		try {
			productController.addProduct(productName, currentProductQuantity, price, category,
					currentBarcode);
			printBarcode();
			Utils.makeToast(AddProductActivity.this, R.string.productAddSuccessful);
			clearTextFields();
		} catch (MelanieBusinessException e) {
			Utils.makeToast(AddProductActivity.this, R.string.productAddFailed);
			// TODO log it
			e.printStackTrace();
		}
	}

	private String generateBarcodeString(int lastItemId) {
		lastItemId++;
		String format = "%06d";
		String barcodeNumber = Utils.getBarcodePrefix() + String.format(format, lastItemId);
		return barcodeNumber;
	}

	private void clearTextFields() {
		Utils.clearInputTextFields(findViewById(R.id.productName), findViewById(R.id.quantity),
				findViewById(R.id.price));
	}

	private Category getSelectedCategory() {
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		return (Category) categorySpinner.getSelectedItem();
	}

	private void printBarcode() {

		if (userWantsToPrintBarcode() && barcodePrintHelper != null) {
			barcodePrintHelper.printBarcode(currentBarcode, null);
		}
	}

	private boolean userWantsToPrintBarcode() {
		CheckBox checkBox = (CheckBox) findViewById(R.id.printBarcodeCheckBox);
		return checkBox.isChecked();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
		if (resultCode == RESULT_OK && requestCode == Utils.Constants.PRINTER_SELECT_REQUEST_CODE) {

			Bundle bundle = intentData.getExtras();
			printerInfo = (Map<String, String>) bundle.get(CodeStrings.PRINTER_INFO);
			if (printerInfo != null) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
				if(barcodePrintHelper != null) {
					barcodePrintHelper.setIsPrinterFound(true);
					barcodePrintHelper.printBarcode(currentBarcode, printerInfo);
				}
			}
		}
		else if (requestCode == Utils.Constants.BLUETOOTH_REQUEST_CODE) {
			bluetoothEnableRefused = resultCode == RESULT_CANCELED;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {

		bundle.putSerializable(CodeStrings.CATEGORIES, categories);
		bundle.putBoolean(CodeStrings.BLUETOOTH_REFUSED, bluetoothEnableRefused);

		super.onSaveInstanceState(bundle);
	}

	private void restoreInstanceState(Bundle bundle){
		if(bundle != null){
			bluetoothEnableRefused = bundle.getBoolean(CodeStrings.BLUETOOTH_REFUSED);
			categories = (ArrayList<Category>) bundle.get(CodeStrings.CATEGORIES);
		}
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(barcodePrintHelper != null){
			barcodePrintHelper.clearResources();
		}
	}

}
