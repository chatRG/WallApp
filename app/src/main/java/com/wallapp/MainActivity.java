package com.wallapp;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.wallapp.activities.MainIntroActivity;
import com.wallapp.activities.SettingsActivity;
import com.wallapp.service.DeleteCache;
import com.wallapp.service.Downloader;
import com.wallapp.utils.DeviceUtils;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static Bitmap mBitmap;
    private static SharedPreferences sharedPref;
    final private String url_def = "https://unsplash.it/";
    final private String url_alt = "https://source.unsplash.com/";
    private final String width = String.valueOf(2048);
    private final String height = String.valueOf(1080);
    @BindView(R.id.sdv)
    SimpleDraweeView draweeView;
    @BindView(android.R.id.content)
    View contentView;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.fab_menu)
    FloatingActionMenu fabMenu;
    @BindView(R.id.share)
    FloatingActionButton fabShare;
    @BindView(R.id.settings)
    FloatingActionButton fabSettings;
    @BindView(R.id.download)
    FloatingActionButton fabDownload;
    @BindView(R.id.setwall)
    FloatingActionButton fabSet;
    @BindView(R.id.rand)
    FloatingActionButton fabRand;
    private String url_ext;
    private String phone_width, phone_height;
    private Uri imageUri;
    private Boolean isDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        firstTime();

        ButterKnife.bind(this);
        init();
    }

    private void firstTime() {
        boolean isFirstStart = sharedPref.getBoolean("firstStart", true);
        if (isFirstStart) {
            Intent i = new Intent(getBaseContext(), MainIntroActivity.class);
            startActivity(i);
            sharedPref.edit().putBoolean("firstStart", false).apply();
            this.finish();
        }
    }

    private void init() {

        phone_width = String.valueOf(new DeviceUtils().getScreenWidth());
        phone_height = String.valueOf(new DeviceUtils().getScreenHeight());

        draweeView.setOnClickListener(this);
        fabSettings.setOnClickListener(this);
        fabDownload.setOnClickListener(this);
        fabSet.setOnClickListener(this);
        fabRand.setOnClickListener(this);
        fabShare.setOnClickListener(this);

        mProgress.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.OVERLAY);

        url_ext = width + "/" + height + "?random";
        setURI(url_def + url_ext);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.download:
                mProgress.setVisibility(ProgressBar.VISIBLE);
                draweeView.setEnabled(false);
                fabMenu.close(true);
                fabMenu.setEnabled(false);

                new Downloader(MainActivity.this).execute(mBitmap);

                isDownloaded = true;
                fabMenu.setEnabled(true);
                draweeView.setEnabled(true);
                mProgress.setVisibility(ProgressBar.GONE);
                break;

            case R.id.sdv:
                new ImageViewer.Builder(MainActivity.this, new String[]{imageUri.toString()})
                        .setStartPosition(0)
                        .show();
                fabMenu.close(true);
                break;

            case R.id.rand:
                mProgress.setVisibility(ProgressBar.VISIBLE);
                fabMenu.close(true);
                fabMenu.setEnabled(false);
                new DeleteCache(MainActivity.this);
                updateURI();
                draweeView.setVisibility(View.GONE);
                Fresco.getImagePipeline().clearCaches();
                draweeView.setImageURI(imageUri);
                draweeView.setVisibility(View.VISIBLE);

                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                ImageRequest imageRequest = ImageRequestBuilder
                        .newBuilderWithSource(imageUri)
                        .setRequestPriority(Priority.HIGH)
                        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                        .build();

                DataSource<CloseableReference<CloseableImage>> dataSource =
                        imagePipeline.fetchDecodedImage(imageRequest, this);

                try {
                    dataSource.subscribe(new BaseBitmapDataSubscriber() {
                                             @Override
                                             public void onNewResultImpl(@Nullable Bitmap bitmap) {
                                                 // Lifetime of Bitmap is limited to this method only.
                                                 mBitmap = bitmap;
                                             }

                                             @Override
                                             public void onFailureImpl(DataSource dataSource) {
                                                 // No cleanup required here.
                                             }
                                         },
                            CallerThreadExecutor.getInstance());
                } catch (Exception e) {
                    showSnack(R.string.error_text);
                } finally {
                    //dataSource.close();
                    isDownloaded = false;
                    mProgress.setVisibility(ProgressBar.GONE);
                }
                break;

            case R.id.setwall:
                fabMenu.close(true);

                Bitmap dstBmp, srcBmp = mBitmap;
                String setType = sharedPref.getString("set_as", "WallApp");

                /* System Set As Wallpaper Intent*/

                if (setType.equals("System")) {
                    File lastFile = getLastModFile(Environment
                            .getExternalStorageDirectory()
                            .getAbsolutePath() + "/Wallapp/");

                    if (lastFile == null || !isDownloaded) {
                        showSnack(R.string.download_image_first);
                        break;
                    }
                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(lastFile), "image/jpeg");
                    intent.putExtra("mimeType", "image/jpeg");
                    this.startActivity(Intent.createChooser(intent, "Set as:"));
                    break;
                }

                /*Set the wallpaper in-app*/

                mProgress.setVisibility(ProgressBar.VISIBLE);
                if (mBitmap.getWidth() >= mBitmap.getHeight()) {
                    dstBmp = Bitmap.createBitmap(srcBmp,
                            srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2, 0,
                            srcBmp.getHeight(),
                            srcBmp.getHeight()
                    );
                } else {
                    dstBmp = Bitmap.createBitmap(srcBmp, 0,
                            srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                            srcBmp.getWidth(),
                            srcBmp.getWidth()
                    );
                }
                setBitmapWallpaper(dstBmp);
                mProgress.setVisibility(ProgressBar.GONE);
                break;

            case R.id.settings:
                fabMenu.close(true);
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.share:
                fabMenu.close(true);
                File lastFile = getLastModFile(Environment
                        .getExternalStorageDirectory()
                        .getAbsolutePath() + "/Wallapp/");

                if (lastFile == null || !isDownloaded) {
                    showSnack(R.string.download_image_first);
                    break;
                }
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(lastFile));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_image)));
                break;
        }
    }

    private void updateURI() {
        boolean isBlur = sharedPref.getBoolean("blur", false);
        boolean isGray = sharedPref.getBoolean("gray", false);
        String category = sharedPref.getString("category", null);
        url_ext = "";

        if (category != null && !category.equals("None")) {
            url_ext = "featured/?" + category.toLowerCase();
            setURI(url_alt + url_ext);
        } else {
            if (isGray)
                url_ext += "g/";
            url_ext += width + "/" + height + "/";
            if (isBlur)
                url_ext += "?blur&random";
            else
                url_ext += "?random";
            setURI(url_def + url_ext);
        }
    }

    private void setURI(String url) {
        imageUri = Uri.parse(url);
    }

    private void setBitmapWallpaper(Bitmap bitmap) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        wallpaperManager.suggestDesiredDimensions(Integer.valueOf(phone_width),
                Integer.valueOf(phone_height));
        try {
            wallpaperManager.clear();
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private File getLastModFile(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModFile.lastModified() < files[i].lastModified()) {
                lastModFile = files[i];
            }
        }
        return lastModFile;
    }

    private void showSnack(int text) {
        Snackbar.make(contentView, text, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (fabMenu.isOpened())
            fabMenu.close(true);
        else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        new DeleteCache(this);
        super.onDestroy();
    }
}