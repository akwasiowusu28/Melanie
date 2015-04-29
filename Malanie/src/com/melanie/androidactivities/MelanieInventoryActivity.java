package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
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
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class MelanieInventoryActivity extends ActionBarActivity {

	private List<Product> allProducts;
	private ProductsAndSalesListViewAdapter<Product> productsAdapter;
	private Handler handler;
	private ProductEntryController productController;
	private List<Product> currentProducts;

	private ArrayAdapter<Category> categoriesAdapter;
	private List<Category> categories;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_melanie_inventory);

		initializeFields();

		setupCategoriesSpinner();
		setupProductsListView();
	}

	private void initializeFields() {
		handler = new Handler(getMainLooper());
		productController = MelanieBusinessFactory.makeProductEntryController();
		getAllCategories();
		categoriesAdapter = new ArrayAdapter<Category>(this,
				android.R.layout.simple_spinner_dropdown_item, categories);
		allProducts = getAllProducts();
		currentProducts = new ArrayList<Product>(allProducts);
		productsAdapter = new ProductsAndSalesListViewAdapter<Product>(this,
				currentProducts);
	}

	private void setupCategoriesSpinner() {
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		categorySpinner.setAdapter(categoriesAdapter);
		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				currentProducts.clear();
				int categoryId = categories.get(position).getId();
				for (Product product : allProducts) {
					Category productCategory = product.getCategory();
					if (productCategory != null
							&& productCategory.getId() == categoryId)
						currentProducts.add(product);
				}
				Utils.notifyListUpdate(productsAdapter, handler);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void setupProductsListView() {
		ListView listView = (ListView) findViewById(R.id.productsListView);
		View headerView = getLayoutInflater().inflate(
				R.layout.layout_saleitems_header, listView, false);

		listView.addHeaderView(headerView);
		listView.setAdapter(productsAdapter);
		Utils.notifyListUpdate(productsAdapter, handler);
	}

	private void getAllCategories() {
		categories = new ArrayList<Category>();
		try {
			List<Category> tempCategories = null;
			tempCategories = productController
					.getAllCategories(new MelanieOperationCallBack<Category>(
							this.getClass().getSimpleName()) {
						@Override
						public void onCollectionOperationSuccessful(
								List<Category> results) {
							Utils.mergeItems(results, categories);
							Utils.notifyListUpdate(categoriesAdapter, handler);
						}
					});
			if (tempCategories != null && !tempCategories.isEmpty())
				categories.addAll(tempCategories);

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO: log it
		}
	}

	private List<Product> getAllProducts() {
		List<Product> products = new ArrayList<Product>();
		if (productController != null)
			try {
				products = productController
						.findAllProducts(new MelanieOperationCallBack<Product>(
								"") {
							@Override
							public void onCollectionOperationSuccessful(
									List<Product> results) {
								Utils.mergeItems(results, allProducts);
								Utils.notifyListUpdate(productsAdapter, handler);
							}

						});
			} catch (MelanieBusinessException e) {
				e.printStackTrace(); // TODO log it
			}
		return products;

	}
}
