package com.wallapp.utils;


import android.content.res.Resources;

public class DeviceMetrics {

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getMaxWidth() {
        return 3840;
    }

    public int getMaxHeight() {
        return 2160;
    }
}