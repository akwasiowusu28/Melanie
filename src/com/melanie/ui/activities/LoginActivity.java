package com.melanie.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.melanie.ui.R;
import com.melanie.ui.support.MelanieAlertDialog;
import com.melanie.ui.support.MelanieAlertDialog.ButtonModes;
import com.melanie.ui.support.Utils;
import com.melanie.business.MelanieSession;
import com.melanie.business.UserController;
import com.melanie.dataaccess.datasource.DataSource;
import com.melanie.entities.User;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

//Dear future author,
//The login in process could better be implemented with the state design pattern
//Indeed, the whole user module should be implemented with the state design pattern

public class LoginActivity extends Activity {

    private UserController userController;
    private MelanieAlertDialog differntDeviceAlertDialog;
    private ProgressDialog progressDialog;
    private Handler handler;
    private MelanieSession session;
    private String currentDeviceId;
    private User user;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeFields();

        if (!session.isInitialized()) {
            initializeSession();
        }

        setupSignupButton();
        setupLoginButton();
        setupDifferntDeviceAlertDialog();

    }

    private void initializeSession() {
        session = BusinessFactory.getSession();

        DataSource dataSource = OpenHelperManager.getHelper(getBaseContext(), DataSource.class);

        // ORMLite
        session.initializeLocal(dataSource);

        // Backendless
        session.initializeCloud(this);
    }

    private void initializeFields() {
        userController = BusinessFactory.makeUserController();
        handler = new Handler(getMainLooper());
        session = BusinessFactory.getSession();
        currentDeviceId = getCurrentDeviceId();
        password = LocalConstants.EMPTY_STRING;
    }

    private void setupSignupButton() {
        Button button = (Button) findViewById(R.id.loginSignupButton);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupLoginButton() {
        Button button = (Button) findViewById(R.id.loginButton);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginAsync().execute(null, null, null);
            }
        });
    }

    private void setupProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.pleaseWait));
        }
    }

    private void login() {
        EditText phoneTextField = (EditText) findViewById(R.id.loginPhoneNumber);
        EditText passwordTextField = (EditText) findViewById(R.id.loginPassword);
        String phoneNumber = phoneTextField.getText().toString();
        password = passwordTextField.getText().toString();
        if (userController != null) {
            try {
                userController.checkPhoneExistOnCloud(phoneNumber, new OperationCallBack<User>() {

                    @Override
                    public void onOperationSuccessful(User user) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (user != null) {
                            user.setPassword(password);
                            handleUserExists(user);
                        } else {
                            postToastToUIThread(R.string.accountLookupFailed);
                        }

                    }
                });
            } catch (MelanieBusinessException e) {
                // TODO Log it
                e.printStackTrace();
            }
        }
    }

    private void handleUserExists(User user) {
        this.user = user;
        if (user.getDeviceId().equals(currentDeviceId)) {
            performLogin();
        } else {
            differntDeviceAlertDialog.show();
        }
    }

    private void postToastToUIThread(final int toastStringId) {
        if (handler != null) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Utils.makeToast(LoginActivity.this, toastStringId);
                }
            });
        }
    }

    private void setupDifferntDeviceAlertDialog() {
        differntDeviceAlertDialog = makeAlertDialog();
        differntDeviceAlertDialog.setTitle(getString(R.string.differentDevices));
        differntDeviceAlertDialog.setMessage(getString(R.string.accountExistForOtherDevice));
        differntDeviceAlertDialog.create();
    }

    private MelanieAlertDialog makeAlertDialog() {
        return new MelanieAlertDialog(this, ButtonModes.YES_NO,
                new MelanieAlertDialog.ButtonMethods() {

                    @Override
                    public void yesButtonOperation() {
                        updateUserDeviceId(new OperationCallBack<OperationResult>() {

                            @Override
                            public void onOperationSuccessful(OperationResult result) {
                                performLogin();
                            }
                        });
                    }

                    @Override
                    public void noButtonOperation() {
                        postToastToUIThread(R.string.accessDenied);
                        this.cancelButtonOperation();
                    }
                });
    }

    private void performLogin() {
        user.setDeviceId(currentDeviceId);
        user.setPassword(password);
        userController.login(user, new OperationCallBack<OperationResult>() {
            @Override
            public void onOperationSuccessful(OperationResult result) {
                if (result.equals(OperationResult.SUCCESSFUL)) {
                    launchMainActivity();
                } else {
                    postToastToUIThread(R.string.loginFailed);
                }
            }
        });
    }

    private void launchMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUserDeviceId(OperationCallBack<OperationResult> operationCallBack) {
        if (userController != null) {
            try {
                userController.updateUser(user, LocalConstants.DEVICEID, currentDeviceId, operationCallBack);
            } catch (MelanieBusinessException e) {
                // TODO log it
                e.printStackTrace();
            }
        }
    }

    private String getCurrentDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getBaseContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        return deviceId;
    }

    private final AsyncTask<Void, Void, Void> loginAsync() {
        return new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                setupProgressDialog();
                if (progressDialog != null) {
                    progressDialog.show();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                login();
                return null;
            }
        };
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onDestroy();
    }

    private class LocalConstants {
        public static final String DEVICEID = "deviceid";
        public static final String EMPTY_STRING = "";
    }

}
