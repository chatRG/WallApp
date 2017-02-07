package com.wallapp.model;


import android.graphics.Bitmap;
import android.util.Log;

public class BitmapStore {
    private Bitmap bitmap;

    public BitmapStore() {
    }

    public Bitmap getBitmap() {
        Log.e("getter", "check");
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        Log.e("setter", "check");
        this.bitmap = bitmap;
    }
}
