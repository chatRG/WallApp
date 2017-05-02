package com.wallapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.wallapp.service.ParseBing;

public class Randomize implements ParseBing.AsyncResponse {

    private static final String URL_DEF = CustomConstants.ALPHA_BASE_URL;
    private static final String URL_ALT = CustomConstants.GAMMA_BASE_URL;
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
        String category = sharedPref.getString("category", null);
        String source = sharedPref.getString("source", "Alpha");

        if (source.equals("Bing daily")) {
            if (!BING_DEF.isEmpty()) {
                setURI(BING_DEF);
            } else {
                new ParseBing(Randomize.this).execute();
            }
            return;
        }

        if (category != null && !category.equals("None")) {
            String url_ext = "featured/?" + category.toLowerCase();
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
