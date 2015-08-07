package com.creativecapsule.paytracker.UI.Activities;

/*
*   BaseActivity.java: This will serve as the Base activity for all the activities in the application
*
*
 */

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.creativecapsule.paytracker.R;


public abstract class BaseActivity extends ActionBarActivity {

    private static final String DEBUG_TAG = "BaseActivity";
    public Boolean isActionBarNotPresent = false;

    private ActionBar bar;
    public String screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_title_bar_color)));

        if (this.isActionBarNotPresent){
            bar.hide();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
