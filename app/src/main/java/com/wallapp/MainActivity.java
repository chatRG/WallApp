package com.wallapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.github.clans.fab.FloatingActionMenu;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.uniquestudio.lowpoly.LowPoly;
import com.wallapp.activities.IntroActivity;
import com.wallapp.activities.SettingsActivity;
import com.wallapp.async.Downloader;
import com.wallapp.async.ParseBing;
import com.wallapp.store.StaticVars;
import com.wallapp.utils.FileUtils;
import com.wallapp.utils.GradientUtils;
import com.wallapp.utils.RandomizeUtils;
import com.wallapp.utils.CommonUtils;
import com.wallapp.utils.ImageUtils;
import com.wallapp.utils.WallpaperUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements Downloader.AsyncResponse,
        ParseBing.AsyncResponse {

    private static String TAG = "MainActivity";

    private static SharedPreferences sharedPref;
    private static String BING_DEF;
    private static Uri imageUri;
    private static Boolean isDownloaded = false;
    private static Boolean isSetAs = false;
    private static Bitmap mBitmap;
    private static Bitmap backupBitmap;

    @BindView(R.id.sdv)
    SimpleDraweeView draweeView;
    @BindView(android.R.id.content)
    View contentView;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.fab_menu)
    FloatingActionMenu fabMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // check connection
        if (!CommonUtils.isNetworkAvailable(this)) {
            CommonUtils.checkNetworkDialog(MainActivity.this);
        } else {
            new ParseBing(MainActivity.this).execute();
        }

        boolean isFirstStart = sharedPref.getBoolean(StaticVars.PREF_FIRST_START, true);
        if (isFirstStart) {
            Intent i = new Intent(getBaseContext(), IntroActivity.class);
            startActivity(i);
            this.finish();
        }
        ButterKnife.bind(this);
    }

    @OnClick(R.id.download)
    public void onDownloadClick() {
        mProgress.setVisibility(ProgressBar.VISIBLE);
        draweeView.setEnabled(false);
        fabMenu.close(true);
        fabMenu.setEnabled(false);

        new Downloader(MainActivity.this).execute(mBitmap);

        isDownloaded = true;
        fabMenu.setEnabled(true);
        draweeView.setEnabled(true);
    }

    @OnClick(R.id.sdv)
    public void onDraweeViewClick() {
        if (mBitmap == null || mBitmap.isRecycled())
            return;

        new ImageViewer.Builder<>(MainActivity.this, new String[]{imageUri.toString()})
                .setStartPosition(0)
                .show();
        fabMenu.close(true);
    }

    @OnClick(R.id.share)
    public void onShareClick() {
        fabMenu.close(true);
        File lastFile = new FileUtils(MainActivity.this).getLastModFile();

        if (lastFile == null || !isDownloaded) {
            CommonUtils.showSnack(this, contentView, R.string.download_image_first);
            return;
        }
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(lastFile));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_image)));
    }

    @OnClick(R.id.edit_wall)
    public void onEditClick() {
        fabMenu.close(true);

        // When edit wallpaper without generating
        if (mBitmap == null || mBitmap.isRecycled()) {
            CommonUtils.showSnack(this, contentView, R.string.generate_image_first);
            return;
        }

        new MaterialDialog.Builder(this)
                .title("Filters")
                .items(R.array.edit_items)
                .positiveText(android.R.string.ok)
                .neutralText(android.R.string.cancel)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog,
                                               Integer[] which, CharSequence[] text) {
                        for (int i : which) {
                            if (i == 0) {
                                mBitmap = ImageUtils.getBlurBitmap(MainActivity.this, mBitmap, 25f);
                            }
                            if (i == 1) {
                                mBitmap = ImageUtils.getGrayBitmap(mBitmap);
                            }
                            if (i == 2) {
                                mBitmap = ImageUtils.getDarkenBitmap(mBitmap);
                            }
                            if (i == 3) {
                                mBitmap = LowPoly.generate(mBitmap, 40);
                            }
                            if (i == 4) {
                                mBitmap = Bitmap.createBitmap(backupBitmap);
                            }
                        }
                        draweeView.setImageBitmap(mBitmap);
                        return true;
                    }
                })
                .cancelable(false)
                .show();
    }

    @OnClick(R.id.setwall)
    public void onSetWallpaperClick() {
        fabMenu.close(true);

        // When set wallpaper is clicked before generating
        if (mBitmap == null) {
            CommonUtils.showSnack(this, contentView, R.string.generate_image_first);
            return;
        }

        mProgress.setVisibility(ProgressBar.VISIBLE);
        isSetAs = true;
        new Downloader(MainActivity.this).execute(mBitmap);
    }

    @OnClick(R.id.settings)
    public void onSettingsClick() {
        fabMenu.close(true);
        Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent1);
    }

    @OnClick(R.id.rand)
    public void onRandomClick() {
        fabMenu.close(true);
        fabMenu.setEnabled(false);

        if (mBitmap != null && mBitmap.isRecycled())
            mBitmap.recycle();

        RandomizeUtils mRand = new RandomizeUtils(MainActivity.this, BING_DEF);
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
                                         backupBitmap = bitmap;
                                     }

                                     @Override
                                     public void onFailureImpl(DataSource dataSource) {
                                         // No cleanup required here.
                                     }
                                 },
                    CallerThreadExecutor.getInstance());
        } catch (Exception e) {
            CommonUtils.showSnack(this, contentView, R.string.error_text);
        } finally {
            //dataSource.close();
            isDownloaded = false;
            isSetAs = false;
            draweeView.setEnabled(true);
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

        if (result && isDownloaded && !isSetAs)
            CommonUtils.showSnack(this, contentView, R.string.download_success);

        else if (isDownloaded && !isSetAs)
            CommonUtils.showSnack(this, contentView, R.string.download_failed);

        else if (isSetAs) {
            File mFile = new FileUtils(MainActivity.this).getLastModFile();
            String setAsType = sharedPref.getString(
                    StaticVars.PREF_SET_AS, StaticVars.APP_NAME);
            WallpaperUtils.setAsWallpaper(MainActivity.this, setAsType, mFile);
            CommonUtils.showSnack(this, contentView, R.string.wallpaper_set_success);
            isSetAs = false;
        }
    }

    @Override
    public void jsonURI(String mURI) {
        BING_DEF = mURI;
    }
}
