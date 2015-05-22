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
import com.melanie.business.MelanieSession;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.entities.User;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class MainActivity extends Activity {

	private static final String IS_FIRST_LAUNCH = "isFirstLaunch";

	private MelanieSession session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!isFirstLaunchAndRedirectedToSignup()) {
			session = MelanieBusinessFactory.getSession();

			if (!session.isInitialized()) {
				// would be nice to push this data initialization down to lower
				// layers
				// Not doing that right now 'cause I hate to import android
				// context in my
				// business or data logic
				DataSource dataSource = OpenHelperManager.getHelper(
						getBaseContext(), DataSource.class);

				// ORMLite
				session.initializeLocal(dataSource);

				// Backendless
				session.initializeCloud(this);
			}

			setContentView(R.layout.activity_main);

			setupMainListView();
			if (!session.isUserLoggedIn())
				loginUser(userOperationCallBack);
		}
	}

	private void setupMainListView() {
		ListView mainListView = (ListView) findViewById(R.id.mainpagelistview);
		mainListView.setAdapter(new NavigationListViewAdapter(this,
				NavigationHelper.getMainPageIcons(), NavigationHelper
						.getMainPageNavigationItems(), NavigationHelper
						.getMainPageNavigationDescription()));
		mainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (session.isUserLoggedIn()) {
					Intent intent = new Intent(MainActivity.this,
							NavigationHelper.getMelanieMainActivities().get(
									position));
					startActivity(intent);
				} else {
					Utils.makeToast(MainActivity.this, R.string.mustbeLoggedIn);
					loginUser(userOperationCallBack);
				}
			}
		});
	}

	private final MelanieOperationCallBack<User> userOperationCallBack = new MelanieOperationCallBack<User>() {

		@Override
		public void onOperationSuccessful(User user) {
			if (!user.isConfirmed()) {
				Intent intent = new Intent(MainActivity.this,
						ConfirmActivity.class);
				startActivity(intent);
			}
		}

	};

	private void loginUser(MelanieOperationCallBack<User> operationCallBack) {
		UserController userController = MelanieBusinessFactory
				.makeUserController();
		try {
			userController.loginSavedUser(operationCallBack);
		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO log it
		}
	}

	private boolean isFirstLaunchAndRedirectedToSignup() {
		SharedPreferences preferences = getSharedPreferences(
				Utils.Constants.PREF_FILE, MODE_PRIVATE);
		boolean isFirstLaunch = preferences.getBoolean(IS_FIRST_LAUNCH, true);
		if (isFirstLaunch) {
			Editor preferencesEditor = preferences.edit();
			preferencesEditor.putBoolean(IS_FIRST_LAUNCH, false);
			preferencesEditor.apply();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
		return isFirstLaunch;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (session != null) {
			session.clearResources();
			session = null;
		}

	}
}
