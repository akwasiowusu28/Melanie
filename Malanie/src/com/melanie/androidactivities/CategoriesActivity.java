package com.melanie.androidactivities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.support.SingleTextViewListAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.exceptions.MelanieArgumentException;

public class CategoriesActivity extends Activity {

	private ProductEntryController productController;
	private List<Category> categories;
	private ArrayAdapter<Category> listAdapter;

	public CategoriesActivity() {
		super();
		productController = MelanieBusinessFactory.makeProductEntryController();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);

		categories = productController.getAllCategories();
		setupListView();
	}

	private void setupListView() {

		ListView categoriesListView = (ListView) findViewById(R.id.categoryList);

		View headerView = getLayoutInflater().inflate(
				R.layout.single_list_header, categoriesListView, false);

		TextView headerTextView = (TextView) headerView.findViewById(R.id.singleListHeader);
		headerTextView.setText(getText(R.string.categoriesList));
		categoriesListView.addHeaderView(headerView);

		listAdapter = new SingleTextViewListAdapter<Category>(this, categories);

		categoriesListView.setAdapter(listAdapter);
	}

	public void addCategory(View view) {
		TextView categoryNameView = (TextView) findViewById(R.id.categoryName);
		String categoryName = categoryNameView.getText().toString();
		try {

			Category category = productController.addCategory(categoryName);
			categories.add(category);
			Utils.notifyListUpdate(listAdapter);
			Utils.clearTextFields(categoryNameView);

		} catch (MelanieArgumentException e) {
			e.printStackTrace();
		}
	}
}
