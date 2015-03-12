package com.melanie.androidactivities;

import com.melanie.androidactivities.support.NavigationHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class ProductsMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productsmain);
	}

	public void launchActivity(View view) {
		Intent intent = new Intent(this, NavigationHelper.getProductActivities().get(
				view.getId()));
		intent.putExtra("viewId", view.getId());
		startActivity(intent);
	}

}
