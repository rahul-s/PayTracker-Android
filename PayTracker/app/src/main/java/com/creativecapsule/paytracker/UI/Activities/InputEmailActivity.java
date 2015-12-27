package com.creativecapsule.paytracker.UI.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creativecapsule.paytracker.Managers.UserAccountManager;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.Utility.Common;

public class InputEmailActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_email);

        Button submitEmailBtn = (Button) findViewById(R.id.email_submit);
        submitEmailBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_submit :
                //submitEmail();
                submitPhoneNumber();
                break;
        }
    }

    private void submitEmail() {
        Common.showLoadingDialog(this);
        EditText emailField = (EditText) findViewById(R.id.email_et);
        String email = emailField.getText().toString();
        if (Common.isValidEmail(email)) {
            UserAccountManager.getSharedManager().submitEmail(email, new UserAccountManager.UserAccountManagerListener() {
                @Override
                public void completed(boolean status) {
                    Common.hideLoadingDialog();
                    if (status) {
                        emailSubmitted();
                    }
                    else {
                        Toast.makeText(InputEmailActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Common.hideLoadingDialog();
            Toast.makeText(InputEmailActivity.this, getResources().getString(R.string.alert_invalid_email), Toast.LENGTH_SHORT).show();
        }
    }

    private void submitPhoneNumber() {
        Common.showLoadingDialog(this);
        EditText phoneEditText = (EditText) findViewById(R.id.phone_et);
        String phoneNumber = phoneEditText.getText().toString();
        if (Common.isValidPhoneNumber(phoneNumber)) {
            UserAccountManager.getSharedManager().submitPhoneNumber(phoneNumber, new UserAccountManager.UserAccountManagerListener() {
                @Override
                public void completed(boolean status) {
                    Common.hideLoadingDialog();
                    if (status) {
                        emailSubmitted();
                    }
                    else {
                        Toast.makeText(InputEmailActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Common.hideLoadingDialog();
            Toast.makeText(InputEmailActivity.this, getResources().getString(R.string.alert_invalid_phone_number), Toast.LENGTH_SHORT).show();
        }
    }

    private void emailSubmitted() {
        //  TODO: go to next screen.
        Intent accountSetupIntent = new Intent(this, AccountSetupActivity.class);
        startActivity(accountSetupIntent);
        finish();
    }
}
