package com.melanie.androidactivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.melanie.androidactivities.support.NavigationListViewAdapter;
import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.business.MelanieBusiness;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.support.MelanieBusinessFactory;

public class MainActivity extends OrmLiteBaseActivity<DataSource> {

	private MelanieBusiness business;

	public MainActivity() {
		super();
		business = MelanieBusinessFactory.makeMelanieBusiness();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ORMLite
		business.initialize(getHelper());

		// Backendless
		business.initializeAlternate(this);

		setContentView(R.layout.activity_main);
		ListView mainListView = (ListView) findViewById(R.id.mainpagelistview);
		mainListView.setAdapter(new NavigationListViewAdapter(this,
				NavigationHelper.getMainPageIcons(), NavigationHelper
						.getMainPageNavigationItems(), NavigationHelper
						.getMainPageNavigationDescription()));
		mainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MainActivity.this, NavigationHelper
						.getMelanieMainActivities().get(position));
				startActivity(intent);

			}

		});
	}
}
