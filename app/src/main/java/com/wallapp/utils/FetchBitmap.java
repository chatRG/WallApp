package com.wallapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

public class FetchBitmap extends AsyncTask<String, Void, Bitmap> {
    private static Bitmap mBitmap;

    @Override
    protected Bitmap doInBackground(String... strings) {
        URL url;
        try {
            url = new URL(strings[0]);
            mBitmap = BitmapFactory.decodeStream(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}