package com.wallapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.wallapp.model.BitmapStore;
import com.wallapp.service.Downloader;
import com.wallapp.service.ParseBing;
import com.wallapp.utils.FileUtils;
import com.wallapp.utils.MiscUtils;
import com.wallapp.utils.Randomize;
import com.wallapp.utils.WallpaperUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        Downloader.AsyncResponse,
        ParseBing.AsyncResponse {

    private static SharedPreferences sharedPref;
    private static String BING_DEF;
    private static Uri imageUri;
    private static Boolean isDownloaded = false;
    private static Boolean isSetAs = false;
    private static BitmapStore imgStore;
    private static Bitmap mBitmap;
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

        draweeView.setOnClickListener(this);
        fabSettings.setOnClickListener(this);
        fabDownload.setOnClickListener(this);
        fabSet.setOnClickListener(this);
        fabRand.setOnClickListener(this);
        fabShare.setOnClickListener(this);

        if (!new MiscUtils(MainActivity.this).isNetworkAvailable()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_network_connection)
                    .setMessage(R.string.no_network_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finishFromChild(MainActivity.this);
                        }
                    })
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_error)
                    .show();
        }

        new ParseBing(MainActivity.this).execute();
        imgStore = new BitmapStore();
    }

    @Override
    public void onClick(View view) {
        MiscUtils miscUtils = new MiscUtils(MainActivity.this);
        switch (view.getId()) {

            case R.id.download:
                mProgress.setVisibility(ProgressBar.VISIBLE);
                draweeView.setEnabled(false);
                fabMenu.close(true);
                fabMenu.setEnabled(false);

                new Downloader(MainActivity.this).execute(imgStore.getBitmap());

                isDownloaded = true;
                fabMenu.setEnabled(true);
                draweeView.setEnabled(true);
                break;

            case R.id.sdv:
                if (mBitmap == null || mBitmap.isRecycled())
                    break;

                new ImageViewer.Builder<>(MainActivity.this, new String[]{imageUri.toString()})
                        .setStartPosition(0)
                        .show();
                fabMenu.close(true);
                break;

            case R.id.rand:
                fabMenu.close(true);
                fabMenu.setEnabled(false);
                if (mBitmap != null)
                    if (mBitmap.isRecycled())
                        mBitmap.recycle();
                Randomize mRand = new Randomize(MainActivity.this, BING_DEF);
                mRand.updateURI();
                imageUri = mRand.getURI();
                draweeView.setVisibility(View.GONE);
                draweeView.setEnabled(false);
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
                        imagePipeline.fetchDecodedImage(imageRequest, MainActivity.this);

                try {
                    dataSource.subscribe(new BaseBitmapDataSubscriber() {
                                             @Override
                                             public void onNewResultImpl(@Nullable Bitmap bitmap) {
                                                 // Lifetime of Bitmap is limited to this method only.
                                                 mBitmap = bitmap;
                                                 imgStore.setBitmap(bitmap);
                                             }

                                             @Override
                                             public void onFailureImpl(DataSource dataSource) {
                                                 // No cleanup required here.
                                             }
                                         },
                            CallerThreadExecutor.getInstance());
                } catch (Exception e) {
                    miscUtils.showSnack(contentView, R.string.error_text);
                } finally {
                    //dataSource.close();
                    isDownloaded = false;
                    isSetAs = false;
                    draweeView.setEnabled(true);
                }
                break;

            case R.id.setwall:
                fabMenu.close(true);

                // When set wallpaper is clicked before generating
                if (imgStore.getBitmap() == null) {
                    miscUtils.showSnack(contentView, R.string.generate_image_first);
                    break;
                }

                mProgress.setVisibility(ProgressBar.VISIBLE);
                isSetAs = true;
                new Downloader(MainActivity.this).execute(imgStore.getBitmap());

                break;

            case R.id.settings:
                fabMenu.close(true);
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

                break;

            case R.id.share:
                fabMenu.close(true);
                File lastFile = new FileUtils(MainActivity.this).getLastModFile();

                if (lastFile == null || !isDownloaded) {
                    miscUtils.showSnack(contentView, R.string.download_image_first);
                    break;
                }
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(lastFile));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_image)));

                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (fabMenu.isOpened())
            fabMenu.close(true);
        else {
            this.finish();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        new FileUtils(MainActivity.this).deleteCache();
        super.onDestroy();
        if (mBitmap != null)
            mBitmap.recycle();
    }

    @Override
    public void processFinish(boolean result) {
        mProgress.setVisibility(ProgressBar.GONE);
        MiscUtils miscUtils = new MiscUtils(MainActivity.this);

        if (result && isDownloaded)
            miscUtils.showSnack(contentView, R.string.download_success);
        else if (isDownloaded)
            miscUtils.showSnack(contentView, R.string.download_failed);

        if (isSetAs) {
            File mFile = new FileUtils(MainActivity.this).getLastModFile();
            String setAsType = sharedPref.getString("set_as", "WallApp");
            new WallpaperUtils(MainActivity.this).setAsWallpaper(setAsType, mFile);
            miscUtils.showSnack(contentView, R.string.wallpaper_set_success);
            isSetAs = false;
        }
        isDownloaded = false;
    }

    @Override
    public void jsonURI(String mURI) {
        BING_DEF = mURI;
    }
}
