package com.wallapp.utils;

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
import android.support.v4.content.ContextCompat;

public class ImageUtils {

    public static Bitmap getBlurBitmap(Context context, Bitmap bitmap, float radius) {

        float intensity = 25f;

        int width = Math.round(bitmap.getWidth());
        int height = Math.round(bitmap.getHeight());

        Bitmap input = Bitmap.createScaledBitmap(bitmap, width, height, false);

        Bitmap output = Bitmap.createBitmap(input);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation inputallocation = Allocation.createFromBitmap(rs, input);
        Allocation outputallocation = Allocation.createFromBitmap(rs, output);
        intrinsicBlur.setRadius(intensity);
        intrinsicBlur.setInput(inputallocation);
        intrinsicBlur.forEach(outputallocation);

        outputallocation.copyTo(output);

        return output;
    }

    public static Bitmap getGrayBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        bitmap.recycle();
        return output;
    }

    public static Bitmap getDarkenBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();

        ColorFilter filter;
        filter = new LightingColorFilter(0xFF999999, 0x00000000);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, new Matrix(), paint);

        return output;
    }
}
