package com.wallapp.async;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

import com.wallapp.store.StaticVars;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Downloader extends AsyncTask<Bitmap, Void, Boolean> {

    public AsyncResponse delegate = null;

    public Downloader(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(Bitmap... bitmaps) {

        try {
            Bitmap bmp = bitmaps[0];
            File root = Environment.getExternalStorageDirectory();
            File mFile = new File(root.getAbsolutePath() + File.separator + StaticVars.APP_NAME);
            if (!mFile.exists())
                mFile.mkdir();
            String fileName = StaticVars.APP_NAME + "_" +
                    new SimpleDateFormat(StaticVars.DATE_FORMAT, Locale.getDefault())
                            .format(new Date()) + ".JPEG";
            File input_file = new File(mFile, fileName);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            InputStream inputStream = new ByteArrayInputStream(stream.toByteArray());
            byte[] data = new byte[1024];
            int count;
            OutputStream outputStream = new FileOutputStream(input_file);
            while ((count = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, count);
            }
            inputStream.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.processFinish(aBoolean);
    }

    public interface AsyncResponse {
        void processFinish(boolean result);
    }
}
