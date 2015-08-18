package com.creativecapsule.paytracker.UI.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.creativecapsule.paytracker.Managers.UserAccountManager;
import com.creativecapsule.paytracker.R;

public class LaunchActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Button getStartedBtn = (Button) findViewById(R.id.get_started_btn);
        getStartedBtn.setOnClickListener(this);

        getSupportActionBar().hide();

        // TODO: redirect to home activity if already logged in

        if (UserAccountManager.getSharedManager().isLoggedIn()) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_started_btn:
                showEmailActivity();
                break;
        }
    }

    private void showEmailActivity() {
        Intent emailActivityIntent = new Intent(this, InputEmailActivity.class);
        startActivity(emailActivityIntent);
        finish();
    }
}
