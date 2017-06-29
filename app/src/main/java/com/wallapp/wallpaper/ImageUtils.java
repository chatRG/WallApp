package com.wallapp.wallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class ImageUtils {

    public static Bitmap getBlurBitmap(Context context, Bitmap bitmap, float radius) {

        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        blurScript.setInput(allIn);
        blurScript.setRadius(radius);
        blurScript.forEach(allOut);

        allOut.copyTo(outBitmap);

        bitmap.recycle();
        rs.destroy();

        return outBitmap;
    }

    public static Bitmap getGrayBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        bitmap.recycle();
        return outBitmap;
    }

    public static Bitmap getDarkenBitmap(Bitmap bitmap, boolean flag) {

        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint(Color.RED);
        ColorFilter filter;
        if (flag)
            // Darken for flag = true
            filter = new LightingColorFilter(0xFF7F7F7F, 0x22222222);
        else
            // Faded for flag = false
            filter = new LightingColorFilter(0xFF7F7F7F, 0x44444444);
        p.setColorFilter(filter);
        canvas.drawBitmap(bitmap, new Matrix(), p);

        return bitmap;
    }
}
