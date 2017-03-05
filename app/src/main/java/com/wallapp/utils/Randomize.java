package com.wallapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public class Randomize {

    private static final String URL_DEF = "https://unsplash.it/";
    private static final String URL_ALT = "https://source.unsplash.com/";
    private static SharedPreferences sharedPref;
    private static String WIDTH;
    private static String HEIGHT;
    private static Uri imageUri;
    private static String BING_DEF;
    Context activity;
    private String url_ext;

    public Randomize(Context activity, String bing) {
        this.activity = activity;
        BING_DEF = bing;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void updateURI() {
        boolean isBlur = sharedPref.getBoolean("blur", false);
        boolean isGray = sharedPref.getBoolean("gray", false);
        String category = sharedPref.getString("category", null);
        String source = sharedPref.getString("source", "Uno");

        DeviceMetrics deviceMetrics = new DeviceMetrics();
        WIDTH = deviceMetrics.getScreenWidth() * 2 + "";
        HEIGHT = deviceMetrics.getScreenHeight() * 1.5 + "";

        if (source.equals("Bing Daily") && !BING_DEF.isEmpty()) {
            setURI(BING_DEF);
            return;
        }

        url_ext = "";

        if (category != null && !category.equals("None")) {
            url_ext = "featured/?" + category.toLowerCase();
            setURI(URL_ALT + url_ext);
        } else {
            if (isGray)
                url_ext += "g/";
            url_ext += WIDTH + "/" + HEIGHT + "/";
            if (isBlur)
                url_ext += "?blur&random";
            else
                url_ext += "?random";
            setURI(URL_DEF + url_ext);
        }
    }

    public Uri getURI() {
        return imageUri;
    }

    private void setURI(String url) {
        imageUri = Uri.parse(url);
    }
}
