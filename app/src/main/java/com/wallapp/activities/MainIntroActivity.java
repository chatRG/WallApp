package com.wallapp.activities;


import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.wallapp.MainActivity;
import com.wallapp.R;

public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);
        setButtonNextVisible(false);
        setSkipEnabled(false);
        setPageScrollDuration(800);

        addSlide(new SimpleSlide.Builder()
                .title("WallApp")
                .description("Generate random wallpapers that fits your device's attitude.")
                .image(R.drawable.ic_intro)
                .background(R.color.intro_1)
                .build());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            addSlide(new SimpleSlide.Builder()
                    .title("Permission, please?")
                    .description("We need just this one permission to download your favorite images.")
                    .image(R.drawable.ic_intro)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .background(R.color.intro_2)
                    .build());

        addSlide(new SimpleSlide.Builder()
                .title("WallApp is all yours now")
                .description("Almost done! Now let's generate some random wallpapers.")
                .image(R.drawable.ic_intro)
                .background(R.color.intro_3)
                .build());

        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                            .edit()
                            .putBoolean("firstStart", false)
                            .apply();
                    Log.e("here3", "hey");
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
