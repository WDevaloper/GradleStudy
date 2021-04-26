package com.github.gradle.mapping;

import android.app.Application;

import com.github.router.runtime.Router;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Router.init();
    }
}
