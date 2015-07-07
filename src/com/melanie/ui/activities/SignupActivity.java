package com.melanie.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.melanie.ui.R;
import com.melanie.ui.support.Utils;
import com.melanie.business.UserController;
import com.melanie.entities.User;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SignupActivity extends AppCompatActivity {

    private UserController userController;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private EditText nameField;
    private EditText phoneField;
    private String phoneNumber;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeFields();
        setupCreateAccountButton();
    }

    private void initializeFields() {
        userController = BusinessFactory.makeUserController();
        passwordField = (EditText) findViewById(R.id.password);
        confirmPasswordField = (EditText) findViewById(R.id.confirmPassword);
        nameField = (EditText) findViewById(R.id.userName);
        phoneField = (EditText) findViewById(R.id.phoneNumber);
    }

    private void setupCreateAccountButton() {
        Button createAccountButton = (Button) findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                signupAsync().execute(null, null, null);
            }
        });

    }

    private final AsyncTask<Void, Void, Void> signupAsync() {
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
                createAccount();
                return null;
            }
        };
    }

    private void setupProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getText(R.string.pleaseWait));
        }
    }

    private void createAccount() {

        passwordField.getText().toString();
        confirmPasswordField.getText().toString();
        nameField.getText().toString();
        phoneNumber = phoneField.getText().toString();

        if (userController != null) {
            try {
                userController.checkPhoneExistOnCloud(phoneNumber, new OperationCallBack<User>() {

                    @Override
                    public void onOperationSuccessful(User result) {
                        if (result == null) {
                            performCreateAccount();
                        } else {
                            Utils.makeToast(SignupActivity.this, R.string.accountExistForPhone);
                            dismissProgressDialog();
                        }
                    }
                });
            } catch (MelanieBusinessException e) {
                // TODO log it
                e.printStackTrace();
            }
        }
    }

    private void performCreateAccount() {
        String name = nameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (passwordsMatch(password, confirmPassword)) {
            createUser(name, phoneNumber, password);
        } else {
            Utils.switchInvalidFieldsBackColor(false, passwordField, confirmPasswordField);
            Utils.makeToast(SignupActivity.this, R.string.passwordsNotMatch);
            dismissProgressDialog();
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void createUser(String name, String phone, String password) {
        try {
            userController.createUser(name, phone, password, getDeviceId(), new OperationCallBack<User>() {

                @Override
                public void onOperationSuccessful(User user) {
                    Utils.switchInvalidFieldsBackColor(true, passwordField, confirmPasswordField);
                    BusinessFactory.getSession().setUser(user);
                    dismissProgressDialog();
                    launchMainActivity();
                }

                @Override
                public void onOperationFailed(Throwable e) {
                    Utils.makeToast(SignupActivity.this, R.string.createAccountFailed);
                }
            });
        } catch (MelanieBusinessException e) {
            // TODO log it
            e.printStackTrace();
        }
    }

    private String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getBaseContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        return deviceId;
    }

    private boolean passwordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
