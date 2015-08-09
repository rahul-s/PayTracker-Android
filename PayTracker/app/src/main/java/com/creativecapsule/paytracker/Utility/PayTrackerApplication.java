package com.creativecapsule.paytracker.Utility;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

/**
 * Created by rahul on 13/04/15.
 */
public class PayTrackerApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        PayTrackerApplication.appContext = getApplicationContext();

        setupParse();
    }

    public static Context getAppContext() {
        return appContext;
    }

    private void setupParse() {
        Parse.initialize(appContext, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
    }

}
