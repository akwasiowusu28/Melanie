package com.melanie.androidactivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;
import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.androidactivities.support.MainPageListViewAdapter;
import com.melanie.business.MelanieBusiness;
import com.melanie.business.concrete.MelanieBusinessImpl;
import com.melanie.dataaccesslayer.datasource.DataSource;

public class MainActivity extends OrmLiteBaseListActivity<DataSource> {
	
	private MelanieBusiness business;
	
	public MainActivity() {
		super();
		business = new MelanieBusinessImpl();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		business.initialize(getHelper());
		setContentView(R.layout.activity_main);
		setListAdapter(new MainPageListViewAdapter(this,
				NavigationHelper.getMainPageIcons(),
				NavigationHelper.getMainPageNavigationItems(),
				NavigationHelper.getMainPageNavigationDescription()));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this,NavigationHelper.getMelanieMainActivities().get(position));
		startActivity(intent);
	}

}
