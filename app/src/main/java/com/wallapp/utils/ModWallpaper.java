package com.wallapp.utils;


import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

public class ModWallpaper {

    private Activity activity;

    public ModWallpaper(Activity activity) {
        this.activity = activity;
    }

    public void setWallpaper(Bitmap bitmap, String setAs, File lastFile) {

        if (setAs.equals("System")) {
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.fromFile(lastFile), "image/jpeg");
            intent.putExtra("mimeType", "image/jpeg");
            activity.startActivity(Intent.createChooser(intent, "Set as:"));
        } else {
            if (bitmap.getWidth() >= bitmap.getHeight()) {
                bitmap = Bitmap.createBitmap(bitmap,
                        bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0,
                        bitmap.getHeight(),
                        bitmap.getHeight());
            } else {
                bitmap = Bitmap.createBitmap(bitmap, 0,
                        bitmap.getHeight() / 2 - bitmap.getWidth() / 2,
                        bitmap.getWidth(),
                        bitmap.getWidth());
            }
            setBitmapWallpaper(bitmap);
        }
    }

    private void setBitmapWallpaper(Bitmap bitmap) {
        DeviceMetrics devMetrics = new DeviceMetrics();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);
        wallpaperManager.suggestDesiredDimensions(devMetrics.getScreenWidth(),
                devMetrics.getScreenHeight());
        try {
            wallpaperManager.clear();
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
