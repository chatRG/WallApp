package com.wallapp.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wallapp.R;

public class EditUtils {

    public static Bitmap editImage(final Context context,
                                   final Bitmap bitmap, final SimpleDraweeView sdv) {

        final Bitmap[] mBitmap = {Bitmap.createBitmap(bitmap)};

        new MaterialDialog.Builder(context)
                .title("Filter")
                .items(R.array.edit_items)
                .cancelable(false)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog,
                                               Integer[] which, CharSequence[] text) {
                        for (int i : which) {
                            if (i == 0) {
                                mBitmap[0] = ImageUtils.getBlurBitmap(context, mBitmap[0], 25f);
                                mBitmap[0] = ImageUtils.getBlurBitmap(context, mBitmap[0], 2f);
                            }
                            if (i == 1)
                                mBitmap[0] = ImageUtils.getGrayBitmap(mBitmap[0]);
                        }

                        sdv.setImageBitmap(mBitmap[0]);
                        return true;
                    }
                })
                .positiveText("OK")
                .neutralText("Cancel")
                .show();

        return mBitmap[0];
    }
}
