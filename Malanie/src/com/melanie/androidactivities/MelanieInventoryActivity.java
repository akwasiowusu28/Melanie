package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.melanie.androidactivities.support.ProductsAndSalesListViewAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.BusinessFactory;
import com.melanie.support.CodeStrings;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class MelanieInventoryActivity extends AppCompatActivity {

	private ArrayList<Product> allProducts;
	private ProductsAndSalesListViewAdapter<Product> productsAdapter;
	private Handler handler;
	private ProductEntryController productController;
	private ArrayList<Product> currentProducts;

	private ArrayAdapter<Category> categoriesAdapter;
	private ArrayList<Category> categories;
	private boolean instanceWasSaved = false;

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_melanie_inventory);

		getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (savedInstanceState != null) {
			instanceWasSaved = true;
			restoreInstanceState(savedInstanceState);
		}
		initializeFields();

		setupCategoriesSpinner();
		setupProductsListView();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.inventory_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

		switchSelectedListItemVisibility(true, menuInfo.position);
		View editView = listView.getChildAt(menuInfo.position).findViewById(R.id.editProductView);
		setupEditView(editView, menuInfo.position);

		unregisterForContextMenu(listView); //To ensure only one edit happens at a time
		return true;
	}

	private void switchSelectedListItemVisibility(boolean showEditView, int position) {
		View mainView = listView.getChildAt(position).findViewById(R.id.mainDisplayRow);
		View editView = listView.getChildAt(position).findViewById(R.id.editProductView);
		Utils.switchViewVisibitlity(showEditView, editView);
		Utils.switchViewVisibitlity(!showEditView, mainView);
	}

	private void initializeFields() {
		handler = new Handler(getMainLooper());
		productController = BusinessFactory.makeProductEntryController();

		if (!instanceWasSaved) {
			categories = new ArrayList<>();
			categories.add(0, new Category(getString(R.string.allCategories)));
			allProducts = new ArrayList<>();
			currentProducts = new ArrayList<Product>();
			getAllCategories();
			getAllProducts();
		}

		categoriesAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, categories);
		productsAdapter = new ProductsAndSalesListViewAdapter<Product>(this, currentProducts, false);
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

				registerForContextMenu(listView); //hook the context menu back up because at this point it should be removed

				Utils.makeToast(MelanieInventoryActivity.this, R.string.productUpdateSucessfull);
				switchSelectedListItemVisibility(false, position);
				Utils.notifyListUpdate(productsAdapter, handler);

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
		listView = (ListView) findViewById(R.id.productsListView);
		View headerView = getLayoutInflater().inflate(R.layout.layout_saleitems_header, listView, false);

		listView.addHeaderView(headerView);
		listView.setAdapter(productsAdapter);
		Utils.notifyListUpdate(productsAdapter, handler);

		registerForContextMenu(listView);

	}

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
				allProducts.addAll(productController.findAllProducts(new OperationCallBack<Product>() {
					@Override
					public void onCollectionOperationSuccessful(List<Product> results) {
						Utils.mergeItems(results, allProducts, false);
						setCurrentProducts(0);
					}

				}));
				Utils.notifyListUpdate(productsAdapter, handler);
			} catch (MelanieBusinessException e) {
				e.printStackTrace(); // TODO log it
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		bundle.putSerializable(CodeStrings.ALLPRODUCTS, allProducts);
		bundle.putSerializable(CodeStrings.CATEGORIES, categories);
		bundle.putSerializable(CodeStrings.CURRENTPRODUCTS, currentProducts);
		super.onSaveInstanceState(bundle);
	}

	@SuppressWarnings("unchecked")
	private void restoreInstanceState(Bundle bundle) {
		if (bundle != null) {
			allProducts = (ArrayList<Product>) bundle.get(CodeStrings.ALLPRODUCTS);
			categories = (ArrayList<Category>) bundle.get(CodeStrings.CATEGORIES);
			currentProducts = (ArrayList<Product>) bundle.get(CodeStrings.CURRENTPRODUCTS);
		}
	}
}
