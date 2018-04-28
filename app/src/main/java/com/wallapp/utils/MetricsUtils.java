package com.wallapp.utils;


import android.content.res.Resources;

import com.wallapp.store.StaticVars;

public class MetricsUtils {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getHDWidth() {
        return StaticVars.HDWidth;
    }

    public static int getHDHeight() {
        return StaticVars.HDHeight;
    }

    public static int getFullHDWidth() {
        return StaticVars.FHDWidth;
    }

    public static int getFullHDHeight() {
        return StaticVars.FHDHeight;
    }

    public static int getMaxWidth() {
        return StaticVars.MAXWidth;
    }

    public static int getMaxHeight() {
        return StaticVars.MAXHeight;
    }
}