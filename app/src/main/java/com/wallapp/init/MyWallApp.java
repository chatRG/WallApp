package com.wallapp.init;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MyWallApp extends Application {
    @Override
    public void onCreate() {

        Fresco.initialize(this);
        super.onCreate();
    }
}
