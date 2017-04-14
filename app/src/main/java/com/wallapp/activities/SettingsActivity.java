package com.wallapp.activities;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wallapp.R;
import com.wallapp.fragments.AppInfoFragment;
import com.wallapp.fragments.OpenLicFragment;
import com.wallapp.utils.DeviceMetrics;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements Preference.OnPreferenceClickListener,
            Preference.OnPreferenceChangeListener {

        private static final String BLUR_KEY = "blur";
        private static final String GRAY_KEY = "gray";
        private static final String CAT_KEY = "category";
        private static final String SET_AS_KEY = "set_as";
        private static final String SRC_KEY = "source";
        private static final String QUALITY_KEY = "quality";
        private static final String INTER_KEY = "interval";
        private static final String OPEN_LICENSES_KEY = "open_licenses";
        private static final String ABOUT_KEY = "about";

        private SharedPreferences sharedPref;
        private Toolbar mToolbar;

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            init();


            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!getFragmentManager().popBackStackImmediate()) {
                        getActivity().finish();
                    }
                }
            });

            findPreference(OPEN_LICENSES_KEY)
                    .setOnPreferenceClickListener(this);
            findPreference(ABOUT_KEY)
                    .setOnPreferenceClickListener(this);
            findPreference(CAT_KEY)
                    .setOnPreferenceChangeListener(this);
            findPreference(SET_AS_KEY)
                    .setOnPreferenceChangeListener(this);
            findPreference(SRC_KEY)
                    .setOnPreferenceChangeListener(this);
            findPreference(QUALITY_KEY)
                    .setOnPreferenceChangeListener(this);
            findPreference(INTER_KEY)
                    .setOnPreferenceChangeListener(this);
        }

        private void init() {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

            if (sharedPref.getString(SRC_KEY, "Uno").equals("Dos")) {
                togglePref(true, false, false);
            } else if (sharedPref.getString(SRC_KEY, "Uno").equals("Bing Daily")) {
                togglePref(false, false, false);
            } else {
                togglePref(false, true, true);
            }

            findPreference(CAT_KEY).setSummary(sharedPref.getString(CAT_KEY, "None"));
            findPreference(SET_AS_KEY).setSummary(sharedPref.getString(SET_AS_KEY, "WallApp"));
            findPreference(SRC_KEY).setSummary(sharedPref.getString(SRC_KEY, "Uno"));
            findPreference(QUALITY_KEY).setSummary(sharedPref.getString(QUALITY_KEY, "Best Fit"));
            findPreference(INTER_KEY).setSummary(sharedPref.getString(INTER_KEY, "None"));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            FragmentTransaction mTransaction = getFragmentManager().beginTransaction();

            switch (preference.getKey()) {

                case OPEN_LICENSES_KEY:

                    mToolbar.setTitle(R.string.open_source_licenses);

                    mTransaction.replace(R.id.content_frame, new OpenLicFragment(), null)
                            .addToBackStack(null).commit();
                    break;

                case ABOUT_KEY:

                    mTransaction.detach(getFragmentManager()
                            .findFragmentById(R.id.content_frame));
                    mTransaction.replace(R.id.content, new AppInfoFragment(), null)
                            .addToBackStack(null).commit();
                    break;
            }
            return true;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            preference.setSummary(o.toString());
            switch (preference.getKey()) {

                case SRC_KEY:
                    if (o.toString().equals("Dos")) {
                        togglePref(true, false, false);
                    } else if (o.toString().equals("Bing Daily")) {
                        togglePref(false, false, false);
                    } else {
                        togglePref(false, true, true);
                    }
                    break;

                case QUALITY_KEY:
                    if (o.toString().equals("HD")) {
                        sharedPref.edit().putInt("height", new DeviceMetrics().getHDHeight()).apply();
                        sharedPref.edit().putInt("width", new DeviceMetrics().getHDWidth()).apply();
                    } else if (o.toString().equals("Full HD")) {
                        sharedPref.edit().putInt("height", new DeviceMetrics().getFullHDHeight()).apply();
                        sharedPref.edit().putInt("width", new DeviceMetrics().getFullHDWidth()).apply();
                    } else if (o.toString().equals("Crazy UHD")) {
                        sharedPref.edit().putInt("height", new DeviceMetrics().getMaxHeight()).apply();
                        sharedPref.edit().putInt("width", new DeviceMetrics().getMaxWidth()).apply();
                    } else {
                        sharedPref.edit().putInt("height", new DeviceMetrics().getScreenHeight()).apply();
                        sharedPref.edit().putInt("width", new DeviceMetrics().getScreenWidth()).apply();
                    }
                    break;

                case INTER_KEY:
                    /*if (o.toString().equals("None")) {
                        getActivity().stopService(new Intent(getActivity(),
                                WallpaperService.class));
                    } else {
                        getActivity().startService(new Intent(getActivity(),
                                WallpaperService.class));
                    }*/
                    break;
            }
            return true;
        }

        private void togglePref(boolean val1, boolean val2, boolean val3) {
            findPreference(CAT_KEY).setEnabled(val1);
            findPreference(BLUR_KEY).setEnabled(val2);
            findPreference(GRAY_KEY).setEnabled(val3);
        }
    }
}