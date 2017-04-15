package com.wallapp.utils;


import android.content.res.Resources;

public class CustomMetrics {

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getHDWidth() {
        return 1280;
    }

    public int getHDHeight() {
        return 720;
    }

    public int getFullHDWidth() {
        return 1920;
    }

    public int getFullHDHeight() {
        return 1080;
    }

    public int getMaxWidth() {
        return 2880;
    }

    public int getMaxHeight() {
        return 1680;
    }
}