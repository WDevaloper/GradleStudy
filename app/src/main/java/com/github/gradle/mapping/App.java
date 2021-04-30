package com.github.gradle.mapping;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.github.router.runtime.Router;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Router.init();
    }
}
