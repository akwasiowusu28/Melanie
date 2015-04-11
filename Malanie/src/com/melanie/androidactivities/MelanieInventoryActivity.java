package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

import com.melanie.androidactivities.support.ProductsAndSalesListViewAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Product;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class MelanieInventoryActivity extends Activity {

	private List<Product> allProducts;
	private ProductsAndSalesListViewAdapter<Product> productsAdapter;
	private Handler handler;
	private ProductEntryController productEntryController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_melanie_inventory);

		initializeFields();
		setupProductsListView();
	}

	private void initializeFields() {
		handler = new Handler(getMainLooper());
		productEntryController = MelanieBusinessFactory
				.makeProductEntryController();
		allProducts = getAllProducts();
		productsAdapter = new ProductsAndSalesListViewAdapter<Product>(this,
				allProducts);
	}

	private void setupProductsListView() {
		ListView listView = (ListView) findViewById(R.id.productsListView);
		View headerView = getLayoutInflater().inflate(
				R.layout.layout_saleitems_header, listView, false);

		listView.addHeaderView(headerView);
		listView.setAdapter(productsAdapter);
	}

	private List<Product> getAllProducts() {
		List<Product> products = new ArrayList<Product>();
		if (productEntryController != null)
			try {
				products = productEntryController
						.findAllProducts(new MelanieOperationCallBack<Product>(
								MelanieInventoryActivity.class.getSimpleName()) {
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
