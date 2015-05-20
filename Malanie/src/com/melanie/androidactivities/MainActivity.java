package com.melanie.androidactivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.androidactivities.support.NavigationListViewAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.MelanieBusiness;
import com.melanie.business.UserController;
import com.melanie.business.concrete.MelanieBusinessImpl;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.exceptions.MelanieBusinessException;

public class MainActivity extends Activity {

	private static final String IS_FIRST_LAUNCH = "isFirstLaunch";
	
	private MelanieBusiness business;
    private final boolean isLoggedIn = false;
    private boolean isDataSourceInitializedFromHere = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!isFirstLaunchAndRedirectedToSignup()){
			business = MelanieBusinessFactory.makeMelanieBusiness();
			
			//would be nice to push the initialization down to lower layers
			DataSource dataSource = OpenHelperManager.getHelper(getBaseContext(), DataSource.class);
			
			// ORMLite
			business.initialize(dataSource);

			// Backendless
			business.initializeAlternate(this);

			isDataSourceInitializedFromHere = true;
			
			setContentView(R.layout.activity_main);
			
			setupMainListView();
			if(!isLoggedIn)
			   loginUser();
		}
	}
	
	private void setupMainListView(){
		ListView mainListView = (ListView) findViewById(R.id.mainpagelistview);
		mainListView.setAdapter(new NavigationListViewAdapter(this,
				NavigationHelper.getMainPageIcons(), NavigationHelper
						.getMainPageNavigationItems(), NavigationHelper
						.getMainPageNavigationDescription()));
		mainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(isLoggedIn){
				Intent intent = new Intent(MainActivity.this, NavigationHelper
						.getMelanieMainActivities().get(position));
				startActivity(intent);
				}
				else{
					Utils.makeToast(MainActivity.this, R.string.mustbeLoggedIn);
					loginUser();
				}

			}

		});
	}
	
	private void loginUser(){
		UserController userController = MelanieBusinessFactory.makeUserController();
		try {
			userController.login();
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); //TODO log it
		}
	}

	
	private boolean isFirstLaunchAndRedirectedToSignup(){
	    SharedPreferences preferences = getSharedPreferences(Utils.Constants.PREF_FILE, MODE_PRIVATE);
	    boolean isFirstLaunch = preferences.getBoolean(IS_FIRST_LAUNCH, true);
	    if(isFirstLaunch){
	    	Editor preferencesEditor = preferences.edit();
	    	preferencesEditor.putBoolean(IS_FIRST_LAUNCH, false);
	    	preferencesEditor.apply();
	    	Intent intent = new Intent(this, SignupActivity.class);
	    	startActivity(intent);
	    	finish();
	    }
	    return isFirstLaunch;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(isDataSourceInitializedFromHere)
		  MelanieBusinessImpl.clearResources();
	}
}
