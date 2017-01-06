package com.wallapp.activities;


import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;

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
                .title(R.string.app_name)
                .description(R.string.intro_desc_1)
                .image(R.drawable.ic_intro)
                .background(R.color.intro_1)
                .build());

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro_title_2)
                    .description(R.string.intro_desc_2)
                    .image(R.drawable.ic_intro)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .background(R.color.intro_2)
                    .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_3)
                .description(R.string.intro_desc_3)
                .image(R.drawable.ic_intro)
                .background(R.color.intro_3)
                .build());

        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position >= 2) {
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                            .edit()
                            .putBoolean("firstStart", false)
                            .apply();
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
