package com.creativecapsule.paytracker.Utility;

import android.app.Application;
import android.content.Context;

/**
 * Created by rahul on 13/04/15.
 */
public class PayTrackerApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        PayTrackerApplication.appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

}
