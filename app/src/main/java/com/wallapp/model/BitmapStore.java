package com.wallapp.model;


import android.graphics.Bitmap;

public class BitmapStore {
    private Bitmap bitmap;

    public BitmapStore() {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
