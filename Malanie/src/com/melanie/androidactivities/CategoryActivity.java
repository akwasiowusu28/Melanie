package com.melanie.androidactivities;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.melanie.business.MelanieBusiness;
import com.melanie.business.MelanieBusinessImpl;
import com.melanie.business.controllers.ProductEntryController;
import com.melanie.business.controllers.ProductEntryControllerImpl;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.support.exceptions.MelanieArgumentException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CategoryActivity extends OrmLiteBaseActivity<DataSource> {

	private ProductEntryController productController;
	private MelanieBusiness business;

	public CategoryActivity() {
		super();
		productController = new ProductEntryControllerImpl();
		business = new MelanieBusinessImpl();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		business.initialize(getHelper());
		setContentView(R.layout.activity_category_activity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addCategory(View view) {
		TextView categoryNameView = (TextView) findViewById(R.id.categoryName);	
	   	String categoryName = categoryNameView.getText().toString();
		try {
			productController.addCategory(categoryName);
		} catch (MelanieArgumentException e) {
			e.printStackTrace();
		}
		TextView resultView = (TextView) findViewById(R.id.resultTempView);
		resultView.setText(categoryName + getString(R.string.addSuccessful));
	}
}
