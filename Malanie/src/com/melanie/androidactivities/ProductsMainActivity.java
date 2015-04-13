package com.melanie.androidactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.melanie.androidactivities.support.NavigationHelper;

public class ProductsMainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productsmain);
	}

	public void launchActivity(View view) {
		Intent intent = new Intent(this, NavigationHelper
				.getProductActivities().get(view.getId()));
		intent.putExtra("viewId", view.getId());
		startActivity(intent);
	}

}
