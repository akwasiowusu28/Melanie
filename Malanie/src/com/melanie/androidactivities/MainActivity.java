package com.melanie.androidactivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.melanie.androidactivities.support.MainPageListViewAdapter;
import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.business.MelanieBusiness;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.support.MelanieBusinessFactory;

public class MainActivity extends OrmLiteBaseListActivity<DataSource> {

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
		setListAdapter(new MainPageListViewAdapter(this,
				NavigationHelper.getMainPageIcons(),
				NavigationHelper.getMainPageNavigationItems(),
				NavigationHelper.getMainPageNavigationDescription()));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, NavigationHelper
				.getMelanieMainActivities().get(position));
		startActivity(intent);
	}
}
