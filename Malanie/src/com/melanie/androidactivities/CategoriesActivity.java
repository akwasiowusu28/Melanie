package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.support.MelanieSingleTextListAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class CategoriesActivity extends ActionBarActivity {

	private ProductEntryController productController;
	private List<Category> categories;
	private ArrayAdapter<Category> listAdapter;
	private Handler handler;

	public CategoriesActivity() {
		super();
		productController = MelanieBusinessFactory.makeProductEntryController();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);

		getAllCategories();
		handler = new Handler(getMainLooper());
		setupListView();
		setupAddCategoryButtonListener();
	}

	private void getAllCategories() {
		categories = new ArrayList<Category>();
		try {
			List<Category> tempCategories = null;
			tempCategories = productController
					.getAllCategories(new MelanieOperationCallBack<Category>() {
						@Override
						public void onCollectionOperationSuccessful(
								List<Category> results) {
							Utils.mergeItems(results, categories);
							Utils.notifyListUpdate(listAdapter, handler);
						}
					});
			if (tempCategories != null && !tempCategories.isEmpty())
				categories.addAll(tempCategories);

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO: log it
		}
	}

	private void setupAddCategoryButtonListener() {
		Button addButton = (Button) findViewById(R.id.addCategoryButton);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TextView categoryNameView = (TextView) findViewById(R.id.categoryName);
				String categoryName = categoryNameView.getText().toString();

				Category category;
				try {
					category = productController.addCategory(categoryName);
					categories.add(category);
					Utils.notifyListUpdate(listAdapter, handler);
					Utils.clearInputTextFields(categoryNameView);
				} catch (MelanieBusinessException e) {
					e.printStackTrace(); // log it
				}
			}
		});
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
}
