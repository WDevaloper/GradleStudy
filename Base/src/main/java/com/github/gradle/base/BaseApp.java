package com.github.gradle.base;

import android.app.Application;
import android.util.Log;

import java.util.Iterator;
import java.util.ServiceLoader;

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ServiceLoader<IModule> modules = ServiceLoader.load(IModule.class);
        Iterator<IModule> iterator = modules.iterator();
        Log.e("app", "onCreate: ");
        while (iterator.hasNext()) {
            IModule module = iterator.next();
            Log.e("app", "onCreate: " + module.toString());
        }
    }
}
