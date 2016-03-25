package com.example.raymond.simpleui;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by raymond on 3/25/16.
 */
public class SimpleUIApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        /* For parse server */
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

    }
}
