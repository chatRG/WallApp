package com.wallapp;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        Fresco.initialize(this);
        super.onCreate();
    }
}
