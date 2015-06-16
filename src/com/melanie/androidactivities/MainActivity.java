package com.melanie.androidactivities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.androidactivities.support.NavigationListViewAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.MelanieSession;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.entities.User;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class MainActivity extends Activity {

    private static final boolean VISIBLE = true;
    private static final boolean GONE = false;
    ProgressBar progressBar;
    private MelanieSession session;
    private boolean isRedirectingForFirstUseAction;
    private boolean isLoggingIn;
    private final OperationCallBack<User> userOperationCallBack = new OperationCallBack<User>() {

        @Override
        public void onOperationSuccessful(User user) {
            switchProgressBarVisisbilityTo(GONE);
            isLoggingIn = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.registerConnectivityReceiver(getApplicationContext());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initializeFields();

        if (!session.isInitialized()) {
            initializeSession();
        }

        isRedirectingForFirstUseAction = needsUserFirstUseAction();

        if (!isRedirectingForFirstUseAction) {
            setContentView(R.layout.activity_main);

            progressBar = (ProgressBar) findViewById(R.id.loginProgress);

            setupMainListView();
            if (!session.isUserLoggedIn()) {
                loginUser(userOperationCallBack);
            }
        }
    }

    private void initializeFields() {
        session = BusinessFactory.getSession();
        isRedirectingForFirstUseAction = false;
        isLoggingIn = false;
    }

    private void initializeSession() {
        // would be nice to push this data initialization down to lower
        // layers
        // Not doing that right now 'cause I hate to import android
        // context in my
        // business or data logic
        DataSource dataSource = OpenHelperManager.getHelper(getBaseContext(), DataSource.class);

        // ORMLite
        session.initializeLocal(dataSource);

        // Backendless
        session.initializeCloud(this);
    }

    private void setupMainListView() {
        ListView mainListView = (ListView) findViewById(R.id.mainpagelistview);
        mainListView.setAdapter(new NavigationListViewAdapter(this, NavigationHelper.getMainPageIcons(),
                NavigationHelper.getMainPageNavigationItems(), NavigationHelper.getMainPageNavigationDescription()));
        mainListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (session.isUserLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, NavigationHelper.getMelanieMainActivities().get(
                            position));
                    startActivity(intent);
                } else {
                    Utils.makeToast(MainActivity.this, R.string.mustbeLoggedIn);
                    if (!isLoggingIn) {
                        loginUser(userOperationCallBack);
                    }
                }
            }
        });
    }

    private void switchProgressBarVisisbilityTo(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void loginUser(OperationCallBack<User> operationCallBack) {
        isLoggingIn = true;
        switchProgressBarVisisbilityTo(VISIBLE);
        UserController userController = BusinessFactory.makeUserController();
        try {
            userController.loginSavedUser(operationCallBack);
        } catch (MelanieBusinessException e) {
            e.printStackTrace(); // TODO log it
        }
    }

    private boolean needsUserFirstUseAction() {

        boolean needsUserFirstUseAction = false;

        User user = getLocalUser();

        needsUserFirstUseAction = user == null || user.isConfirmed() == false;

        if (needsUserFirstUseAction) {
            Intent intent = null;

            if (user == null) {
                intent = new Intent(this, LoginActivity.class);
            } else if (!user.isConfirmed()) {
                intent = new Intent(this, ConfirmActivity.class);
                intent.putExtra(LocalConstants.PHONE_NUMBER, user.getPhone());
            }
            startActivity(intent);
            finish();
        }
        return needsUserFirstUseAction;
    }

    private User getLocalUser() {
        User user = null;
        try {
            user = BusinessFactory.makeUserController().getLocalUser();
        } catch (MelanieBusinessException e) {
            // TODO log it
            e.printStackTrace();
        }
        return user;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isFinishing() && !isRedirectingForFirstUseAction && session != null) {
            session.clearResources();
            Utils.unregisterConnectivityReceiver(getApplicationContext());
            session = null;
        }

    }

    private class LocalConstants {
        public static final String PHONE_NUMBER = "phoneNumber";
    }
}
