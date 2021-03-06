package com.melanie.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.melanie.ui.R;
import com.melanie.ui.support.MelanieAlertDialog;
import com.melanie.ui.support.MelanieAlertDialog.ButtonModes;
import com.melanie.ui.support.Utils;
import com.melanie.business.MelanieSession;
import com.melanie.business.UserController;
import com.melanie.entities.User;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

import java.util.Calendar;

public class ConfirmActivity extends AppCompatActivity {

    private Button confirmButton;
    private String confirmCode;
    private UserController userController;
    private MelanieAlertDialog confirmAlertDialog;
    private String phoneNumber;
    private boolean confirmFieldsDisabled;
    private EditText confirmTextField;
    private TextView confirmLabel;
    private Button sendCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        initializeFields();
        setupConfirmButton();
        showConfirmAlertDialog();
        setupSendConfirmationCodeButton();
    }

    private void initializeFields() {
        userController = BusinessFactory.makeUserController();
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra(LocalConstants.PHONE_NUMBER);
        confirmFieldsDisabled = false;
        confirmTextField = (EditText) findViewById(R.id.confirmTextField);
        confirmLabel = (TextView) findViewById(R.id.confirmLabel);
        sendCodeButton = (Button) findViewById(R.id.sendConfirmCode);

    }

    private void setupConfirmButton() {
        confirmButton = (Button) findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (updateUser().equals(OperationResult.SUCCESSFUL)) {
                    launchMainActivity();
                }
            }
        });
    }

    private void setupSendConfirmationCodeButton() {
        sendCodeButton = (Button) findViewById(R.id.sendConfirmCode);
        sendCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmAlertDialog.show();
            }
        });
    }

    private void showConfirmAlertDialog() {
        confirmAlertDialog = makeAlertDialog();
        confirmAlertDialog.setTitle(getString(R.string.confirmNumber));
        confirmAlertDialog
                .setMessage(getString(R.string.confirmNumberQuestion));
        confirmAlertDialog.show();
    }

    private MelanieAlertDialog makeAlertDialog() {
        return new MelanieAlertDialog(this,
                ButtonModes.YES_NO,
                new MelanieAlertDialog.ButtonMethods() {

                    @Override
                    public void yesButtonOperation() {
                        sendConfirmSMS();
                        if (confirmFieldsDisabled) {
                            changeConfirmFieldsVisibility(true);
                            sendCodeButton.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void noButtonOperation() {
                        confirmFieldsDisabled = true;
                        changeConfirmFieldsVisibility(false);
                        sendCodeButton.setVisibility(View.VISIBLE);
                        this.cancelButtonOperation();
                    }
                });
    }

    private void changeConfirmFieldsVisibility(boolean makeVisible) {
        Utils.switchViewVisibitlity(makeVisible, confirmButton, confirmTextField, confirmLabel);
    }

    private OperationResult updateUser() {
        OperationResult result = OperationResult.FAILED;
        String enteredConfirmCode = ((EditText) findViewById(R.id.confirmTextField))
                .getText().toString();
        if (enteredConfirmCode.equals(confirmCode)) {
            try {
                MelanieSession session = BusinessFactory.getSession();
                User user = session.getUser();
                user.setConfirmed(true);
                userController.updateUser(user, LocalConstants.ISCONFIRMED, true, null);
            } catch (MelanieBusinessException e) {
                // TODO log it
                e.printStackTrace();
            }
            result = OperationResult.SUCCESSFUL;
        }else {
            Utils.makeToast(this, R.string.confirmation_failed);
        }
        return result;
    }

    private void sendConfirmSMS() {
        generateConfirmCode();

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(phoneNumber, null, getConfirmMessage(),
                null, null);
    }

    private String getConfirmMessage() {
        return LocalConstants.CONFIRM_SMS_MESSAGE + confirmCode;
    }

    private void generateConfirmCode() {
        String currentTimeString = String.valueOf(Calendar.getInstance()
                .getTimeInMillis());
        confirmCode = currentTimeString
                .substring(currentTimeString.length() - 4);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class LocalConstants {
        public static final String CONFIRM_SMS_MESSAGE = "Your Melanie confirmation code is: ";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String ISCONFIRMED = "isconfirmed";
    }

}
