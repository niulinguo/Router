package com.lingo.router;

import android.app.Application;

import com.lingo.router.runtime.Router;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Router.INSTANCE.init();
    }
}
