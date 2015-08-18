package com.creativecapsule.paytracker.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creativecapsule.paytracker.Managers.UserAccountManager;
import com.creativecapsule.paytracker.R;
import com.creativecapsule.paytracker.Utility.Common;

public class AccountSetupActivity extends BaseActivity implements View.OnClickListener {

    private boolean isLoginMode;
    private EditText etName, etPassword, etRePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        etName = (EditText) findViewById(R.id.name_et);
        etPassword = (EditText) findViewById(R.id.password_et);
        etRePassword = (EditText) findViewById(R.id.password_confirm_et);

        if (UserAccountManager.getSharedManager().isEmailRegistered()) {
            isLoginMode = true;
            etName.setVisibility(View.GONE);
            etRePassword.setVisibility(View.GONE);
        }
        else {
            isLoginMode = false;
        }

        Button submitBtn = (Button) findViewById(R.id.account_setup_submit);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account_setup_submit:
                submitDetails();
                break;
        }
    }

    private void submitDetails() {
        if (isLoginMode) {
            // Do login
            String password = etPassword.getText().toString();
            if (password == null || password.equals("")) {
                Toast.makeText(this, getResources().getString(R.string.alert_passwords_empty), Toast.LENGTH_SHORT).show();
                return;
            }
            Common.showLoadingDialog(this);
            UserAccountManager.getSharedManager().loginUser(password, new UserAccountManager.UserAccountManagerListener() {
                @Override
                public void completed(boolean status) {
                    Common.hideLoadingDialog();
                    if (status) {
                        accountSetupCompleted();
                    }
                    else {
                        Toast.makeText(AccountSetupActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            // Do Sign up
            String password = etPassword.getText().toString();
            String rePassword = etRePassword.getText().toString();
            String name = etName.getText().toString();

            if (password == null || password.equals("")) {
                Toast.makeText(this, getResources().getString(R.string.alert_passwords_empty), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(rePassword)) {
                Toast.makeText(this, getResources().getString(R.string.alert_passwords_different), Toast.LENGTH_SHORT).show();
                return;
            }
            if (name == null || name.equals("")) {
                Toast.makeText(this, getResources().getString(R.string.alert_name_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            Common.showLoadingDialog(this);
            UserAccountManager.getSharedManager().registerUser(name, password, new UserAccountManager.UserAccountManagerListener() {
                @Override
                public void completed(boolean status) {
                    Common.hideLoadingDialog();
                    if (status) {
                        accountSetupCompleted();
                    }
                    else {
                        Toast.makeText(AccountSetupActivity.this, getResources().getString(R.string.alert_failed_task), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void accountSetupCompleted() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
