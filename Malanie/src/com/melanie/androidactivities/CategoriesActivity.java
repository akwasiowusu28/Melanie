package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.support.MelanieSingleTextListAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

@SuppressWarnings("unchecked")
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

		categories = getAllCategories();

		setupListView();
	}

	private List<Category> getAllCategories() {
		categories = new ArrayList<Category>();
		try {
			List<Category> tempCategories = null;
			tempCategories = productController
					.getAllCategories(new MelanieOperationCallBack() {
						@Override
						public <T> void onOperationSuccessful(List<T> results) {
							categories.clear();
							categories.addAll((List<Category>) results);
							Utils.notifyListUpdate(listAdapter);
						}
					});
			if (tempCategories != null && !tempCategories.isEmpty())
				categories.addAll(tempCategories);

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO: log it
		}
		return categories;
	}

	private void setupListView() {

		ListView categoriesListView = (ListView) findViewById(R.id.categoryList);

		View headerView = getLayoutInflater().inflate(
				R.layout.single_list_header, categoriesListView, false);

		TextView headerTextView = (TextView) headerView
				.findViewById(R.id.singleListHeader);
		headerTextView.setText(getText(R.string.categoriesList));
		categoriesListView.addHeaderView(headerView);

		listAdapter = new MelanieSingleTextListAdapter<Category>(this,
				categories);

		categoriesListView.setAdapter(listAdapter);
	}

	public void addCategory(View view) {
		TextView categoryNameView = (TextView) findViewById(R.id.categoryName);
		String categoryName = categoryNameView.getText().toString();

		Category category;
		try {
			category = productController.addCategory(categoryName);
			categories.add(category);
			Utils.notifyListUpdate(listAdapter);
			Utils.clearInputTextFields(categoryNameView);
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // log it
		}

	}
}
