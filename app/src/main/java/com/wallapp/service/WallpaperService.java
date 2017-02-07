package com.wallapp.service;


import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.wallapp.utils.DeviceMetrics;
import com.wallapp.utils.FetchBitmap;
import com.wallapp.utils.Randomize;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class WallpaperService extends Service {

    private static final long SET_INTERVAL = 1000 * 60 * 60 * 24; //1 day
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }

        long interval = SET_INTERVAL;
        String tempInter = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getString("interval", "None");
        if (tempInter.equals("Weekly"))
            interval *= 7;
        mTimer.scheduleAtFixedRate(new SetWallpaperTask(), 0, interval);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class SetWallpaperTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    WallpaperManager wm = WallpaperManager.getInstance(getApplicationContext());
                    DeviceMetrics devMetrics = new DeviceMetrics();
                    try {
                        wm.suggestDesiredDimensions(devMetrics.getScreenWidth(),
                                devMetrics.getScreenHeight());
                        wm.setBitmap(extractBitmap());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        private Bitmap extractBitmap() {
            String bing = null;
            Bitmap bitmap = null;

            try {
                bing = new ParseJSON().execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            Randomize mRand = new Randomize(getApplicationContext(), bing);
            mRand.updateURI();
            try {
                bitmap = new FetchBitmap().execute(mRand.getURI().toString()).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}