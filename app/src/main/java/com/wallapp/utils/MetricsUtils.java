package com.wallapp.utils;


import android.content.res.Resources;

import com.wallapp.CustomConstants;

public class MetricsUtils {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getHDWidth() {
        return CustomConstants.HDWidth;
    }

    public static int getHDHeight() {
        return CustomConstants.HDHeight;
    }

    public static int getFullHDWidth() {
        return CustomConstants.FHDWidth;
    }

    public static int getFullHDHeight() {
        return CustomConstants.FHDHeight;
    }

    public static int getMaxWidth() {
        return CustomConstants.MAXWidth;
    }

    public static int getMaxHeight() {
        return CustomConstants.MAXHeight;
    }
}