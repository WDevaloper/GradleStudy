package com.github.gradle.mapping;

import android.content.Context;

import androidx.multidex.MultiDex;

import com.github.gradle.MainActivity;
import com.github.gradle.base.BaseApp;
import com.github.router.runtime.Router;

public class App extends BaseApp {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Router.init();
        MainActivity.CrashUncaughtExceptionHandler.init(this);
    }
}
