package com.lingo.router;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.lingo.router.runtime.Router;

public class MyApp extends Application {

    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Router.INSTANCE.init();

        try {
            final ApplicationInfo info = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            final String mtaChannel = info.metaData.getString("MTA_CHANNEL");
            printLog("mtaChannel:" + mtaChannel);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printLog(String msg) {
        Log.i(TAG, msg);
    }
}
