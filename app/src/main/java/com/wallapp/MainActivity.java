package com.wallapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
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
import com.wallapp.service.ParseJSON;
import com.wallapp.utils.DeleteCache;
import com.wallapp.utils.ModWallpaper;
import com.wallapp.utils.Randomize;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        Downloader.AsyncResponse,
        ParseJSON.AsyncResponse {

    private static SharedPreferences sharedPref;
    private static String BING_DEF;
    private static Uri imageUri;
    private static Boolean isDownloaded = false;
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

        //mProgress.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.OVERLAY);

        new ParseJSON(this).execute();
        imgStore = new BitmapStore();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.download:
                mProgress.setVisibility(ProgressBar.VISIBLE);
                draweeView.setEnabled(false);
                fabMenu.close(true);
                fabMenu.setEnabled(false);

                new Downloader(this).execute(imgStore.getBitmap());

                isDownloaded = true;
                fabMenu.setEnabled(true);
                draweeView.setEnabled(true);
                break;

            case R.id.sdv:
                new ImageViewer.Builder(MainActivity.this, new String[]{imageUri.toString()})
                        .setStartPosition(0)
                        .show();
                fabMenu.close(true);
                break;

            case R.id.rand:
                fabMenu.close(true);
                fabMenu.setEnabled(false);
                new DeleteCache(MainActivity.this);
                Randomize mRand = new Randomize(this, BING_DEF);
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
                        imagePipeline.fetchDecodedImage(imageRequest, this);

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
                    showSnack(R.string.error_text);
                } finally {
                    //dataSource.close();
                    isDownloaded = false;
                    draweeView.setEnabled(true);
                }
                break;

            case R.id.setwall:
                fabMenu.close(true);
                mProgress.setVisibility(ProgressBar.VISIBLE);

                File mFile = getLastModFile();
                String setType = sharedPref.getString("set_as", "WallApp");

                if (setType.equals("System") && (mFile == null || !isDownloaded)) {
                    showSnack(R.string.download_image_first);
                    mProgress.setVisibility(ProgressBar.GONE);
                    break;
                }

                new ModWallpaper(this).setWallpaper(mBitmap, setType, mFile);

                if (setType.equals("WallApp")) {
                    showSnack(R.string.wallpaper_set_success);
                }
                mProgress.setVisibility(ProgressBar.GONE);
                break;

            case R.id.settings:
                fabMenu.close(true);
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.share:
                fabMenu.close(true);
                File lastFile = getLastModFile();

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

    @Nullable
    private File getLastModFile() {
        String dirPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath() + "/Wallapp/";
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
        TSnackbar snackbar = TSnackbar
                .make(contentView, text, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(R.drawable.ic_intro, 48);
        snackbar.setIconPadding(8);
        snackbar.setMaxWidth(3000);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#546E7A"));
        TextView textView = (TextView)
                snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#EEEEEE"));
        snackbar.show();

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
        new DeleteCache(this);
        super.onDestroy();
    }

    @Override
    public void processFinish(boolean result) {
        mProgress.setVisibility(ProgressBar.GONE);
        if (result)
            showSnack(R.string.download_success);
        else
            showSnack(R.string.download_failed);
    }

    @Override
    public void jsonURI(String mURI) {
        BING_DEF = mURI;
    }
}