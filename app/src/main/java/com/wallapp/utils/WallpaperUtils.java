package com.wallapp.utils;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

public class WallpaperUtils {

    private Activity activity;

    public WallpaperUtils(Activity activity) {
        this.activity = activity;
    }

    public void setAsWallpaper(String setAs, File lastFile) {

        if (setAs.equals("System")) {
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.fromFile(lastFile), "image/jpeg");
            intent.putExtra("mimeType", "image/jpeg");
            activity.startActivity(Intent.createChooser(intent, "Set wallpaper with"));
        } else {
            setWallpaper(lastFile);
        }
    }

    private void setWallpaper(File lastFile) {
        MetricsUtils metricsUtils = new MetricsUtils();
        int height = metricsUtils.getScreenHeight();
        int width = metricsUtils.getScreenWidth() << 1;

        String imagePath = lastFile.getAbsolutePath();

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(imagePath, options);

        WallpaperManager wm = WallpaperManager.getInstance(activity);
        try {
            wm.setBitmap(decodedSampleBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lastFile.delete();
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
