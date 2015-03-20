package com.melanie.androidactivities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.exceptions.MelanieArgumentException;

public class CategoriesActivity extends Activity {

	private ProductEntryController productController;

	public CategoriesActivity() {
		super();
		productController = MelanieBusinessFactory.makeProductEntryController();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
		List<Category> categories = productController.getAllCategories();

		ListView categoriesListView = (ListView) findViewById(R.id.categoryList);
		categoriesListView.setAdapter(new ArrayAdapter<Category>(this,
				android.R.layout.simple_list_item_1, categories));
	}

	public void addCategory(View view) {
		TextView categoryNameView = (TextView) findViewById(R.id.categoryName);
		String categoryName = categoryNameView.getText().toString();
		try {
			productController.addCategory(categoryName);
		} catch (MelanieArgumentException e) {
			e.printStackTrace();
		}
	}
}
