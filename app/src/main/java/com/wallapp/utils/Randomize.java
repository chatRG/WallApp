package com.wallapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.wallapp.service.ParseBing;

public class Randomize implements ParseBing.AsyncResponse {

    private static final String URL_DEF = CustomConstants.UNO_BASE_URL;
    private static final String URL_ALT = CustomConstants.DOS_BASE_URL;
    private static String BING_DEF;
    private static SharedPreferences sharedPref;
    private static Uri imageUri;
    private Context context;

    public Randomize(Context context, String bing) {
        this.context = context;
        BING_DEF = bing;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void updateURI() {
        boolean isBlur = sharedPref.getBoolean("blur", false);
        boolean isGray = sharedPref.getBoolean("gray", false);
        String category = sharedPref.getString("category", null);
        String source = sharedPref.getString("source", "Uno");

        MetricsUtils metricsUtils = new MetricsUtils();
        String WIDTH = sharedPref.getInt("width", metricsUtils.getScreenWidth()) + "";
        String HEIGHT = sharedPref.getInt("height", metricsUtils.getScreenHeight()) + "";

        if (source.equals("Bing daily")) {
            if (!BING_DEF.isEmpty()) {
                setURI(BING_DEF);
            } else {
                new ParseBing(Randomize.this).execute();
            }
            return;
        }

        String url_ext = "";

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

    @Override
    public void jsonURI(String mURI) {
        BING_DEF = mURI;
    }
}
