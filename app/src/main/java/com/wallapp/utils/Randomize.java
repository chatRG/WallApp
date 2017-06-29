package com.wallapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.wallapp.CustomConstants;
import com.wallapp.async.ParseBing;

public class Randomize implements ParseBing.AsyncResponse {

    private static final String URL_ALT = CustomConstants.ALPHA_BASE_URL;
    private static String BING_DEF;
    private static SharedPreferences sharedPref;
    private static Uri imageUri;
    private static Bitmap mBitmap;
    private Context context;

    public Randomize(Context context, String bing) {
        this.context = context;
        BING_DEF = bing;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void updateURI() {
        String category = sharedPref.getString("category", "None");
        String source = sharedPref.getString("source", CustomConstants.SRC_ALPHA);

        int width = sharedPref.getInt("width", MetricsUtils.getScreenWidth());
        int height = sharedPref.getInt("height", MetricsUtils.getScreenHeight());

        if (source.equals(CustomConstants.SRC_BING)) {
            if (!BING_DEF.isEmpty()) {
                setURI(BING_DEF);
            } else {
                new ParseBing(Randomize.this).execute();
            }
        } else if (source.equals(CustomConstants.SRC_ALPHA)) {
            String url_ext = width + "x" + height;
            if (!category.equals("None")) {
                url_ext += "/?" + category.toLowerCase();
            }
            setURI(URL_ALT + url_ext);
        }
    }

    public void createGradient() {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFF616261, 0xFF131313});
        mBitmap = Bitmap.createBitmap(MetricsUtils.getScreenWidth(),
                MetricsUtils.getScreenHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        gd.draw(canvas);
    }

    public Bitmap getBitmapGradient() {
        return mBitmap;
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
