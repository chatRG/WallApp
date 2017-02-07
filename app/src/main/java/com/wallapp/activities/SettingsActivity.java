package com.wallapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.wallapp.R;
import com.wallapp.service.WallpaperService;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Settings");
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
        private static final String INTER_KEY = "interval";
        private static final String ABOUT_KEY = "about";

        private SharedPreferences sharedPref;

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            init();

            findPreference(ABOUT_KEY)
                    .setOnPreferenceClickListener(this);
            findPreference(CAT_KEY)
                    .setOnPreferenceChangeListener(this);
            findPreference(SET_AS_KEY)
                    .setOnPreferenceChangeListener(this);
            findPreference(SRC_KEY)
                    .setOnPreferenceChangeListener(this);
            findPreference(INTER_KEY)
                    .setOnPreferenceChangeListener(this);
        }

        private void init() {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
            findPreference(INTER_KEY).setSummary(sharedPref.getString(INTER_KEY, "None"));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()) {
                case ABOUT_KEY:
                    new AlertDialog.Builder(getActivity())
                            .setTitle("About")
                            .setMessage(getString(R.string.about_details))
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //some code
                                }
                            })
                            .setCancelable(false)
                            .show();
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
                case INTER_KEY:
                    if (o.toString().equals("None")) {
                        getActivity().stopService(new Intent(getActivity(),
                                WallpaperService.class));
                    } else {
                        getActivity().startService(new Intent(getActivity(),
                                WallpaperService.class));
                    }
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