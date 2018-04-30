package com.wallapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.wallapp.store.StaticVars;
import com.wallapp.async.ParseBing;

public class RandomizeUtils implements ParseBing.AsyncResponse {

    private static final String URL_ALT = StaticVars.ALPHA_BASE_URL;
    private static String BING_DEF;
    private static SharedPreferences sharedPref;
    private static Uri imageUri;
    private Context context;

    public RandomizeUtils(Context context, String bing) {
        this.context = context;
        BING_DEF = bing;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void updateURI() {
        String category = sharedPref.getString("category", "None");
        String source = sharedPref.getString("source", StaticVars.SRC_UNSPLASH);

        int width = sharedPref.getInt("width", MetricsUtils.getScreenWidth());
        int height = sharedPref.getInt("height", MetricsUtils.getScreenHeight());

        if (source.equals(StaticVars.SRC_BING)) {
            if (!BING_DEF.isEmpty()) {
                setURI(BING_DEF);
            } else {
                new ParseBing(RandomizeUtils.this).execute();
            }
        } else if (source.equals(StaticVars.SRC_UNSPLASH)) {
            String url_ext = width + "x" + height;
            if (!category.equals("None")) {
                url_ext += "/?" + category.toLowerCase();
            }
            setURI(URL_ALT + url_ext);
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
