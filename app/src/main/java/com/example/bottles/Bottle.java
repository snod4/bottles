package com.example.bottles;

import android.app.Application;
import android.util.Log;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class Bottle extends Application {

    App taskApp;
    Realm uiThreadRealm;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Realm SDK
        Realm.init(this);
        taskApp = new App(new AppConfiguration.Builder(BuildConfig.MONGODB_REALM_APP_ID)
                        .build());

        // Enable more logging in debug mode
        if (BuildConfig.DEBUG) {
            RealmLog.setLevel(LogLevel.ALL);
        }

        Log.v("Bottle", "Initialized the Realm App configuration for: ${taskApp.configuration.appId}");

    }
}
