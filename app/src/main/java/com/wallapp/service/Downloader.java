package com.wallapp.service;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

public class Downloader extends AsyncTask<Bitmap, Void, Boolean> {
    private Activity activity;

    public Downloader(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Bitmap... bitmaps) {

        try {
            Bitmap bmp = bitmaps[0];
            File root = Environment.getExternalStorageDirectory();
            File mFile = new File(root.getAbsolutePath() + "/Wallapp");
            if (!mFile.exists())
                mFile.mkdir();
            String fileName = DateFormat.getDateTimeInstance().format(new Date());
            fileName = "Wallapp_" + fileName + ".JPEG";
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
            Thread.sleep(4000);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean value) {
        super.onPostExecute(value);
        if (value) {
            Toast.makeText(activity, "Downloaded Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
