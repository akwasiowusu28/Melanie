package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_melanie_inventory);

		if (savedInstanceState != null) {
			instanceWasSaved = true;
			restoreInstanceState(savedInstanceState);
		}
		initializeFields();

		setupCategoriesSpinner();
		setupProductsListView();
	}

	private void initializeFields() {
		handler = new Handler(getMainLooper());
		productController = BusinessFactory.makeProductEntryController();

		categories = new ArrayList<>();
		categories.add(0, new Category(getString(R.string.allCategories)));
		allProducts = new ArrayList<>();
		currentProducts = new ArrayList<Product>();

		categoriesAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item, categories);
		productsAdapter = new ProductsAndSalesListViewAdapter<Product>(this, currentProducts, false);

		if (!instanceWasSaved) {
			getAllCategories();
			getAllProducts();
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
		ListView listView = (ListView) findViewById(R.id.productsListView);
		View headerView = getLayoutInflater().inflate(R.layout.layout_saleitems_header, listView, false);

		listView.addHeaderView(headerView);
		listView.setAdapter(productsAdapter);
		Utils.notifyListUpdate(productsAdapter, handler);
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
