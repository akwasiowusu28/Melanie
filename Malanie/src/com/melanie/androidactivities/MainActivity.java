package com.melanie.androidactivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.melanie.androidactivities.support.NavigationListViewAdapter;
import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.business.MelanieBusiness;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.support.MelanieBusinessFactory;

public class MainActivity extends Activity {

	private MelanieBusiness business;
   
	public MainActivity() {
		super();
		business = MelanieBusinessFactory.makeMelanieBusiness();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//would be nice to push the initialization down to lower layers
		DataSource dataSource = OpenHelperManager.getHelper(getBaseContext(), DataSource.class);
		
		// ORMLite
		business.initialize(dataSource);

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
	
	private void loginUser(){
		UserController userController = MelanieBusinessFactory.makeUserController();
		userController.login(name, password, operationCallBack)
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		business.clearResources();
	}
	
	
}
