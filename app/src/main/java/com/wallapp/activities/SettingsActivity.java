package com.wallapp.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.wallapp.R;

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
                .replace(R.id.content_frame, new SetFragment())
                .commit();
    }

    public static class SetFragment extends PreferenceFragment
            implements Preference.OnPreferenceClickListener,
            Preference.OnPreferenceChangeListener {

        private final String BLUR_KEY = "blur";
        private final String GRAY_KEY = "gray";
        private final String CAT_KEY = "category";
        private final String SET_AS_KEY = "set_as";
        private final String SRC_KEY = "source";
        private final String ABOUT_KEY = "about";

        public SetFragment() {
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
                    if (o.toString().equals("Uno")) {
                        togglePref(false);
                    } else {
                        togglePref(true);
                    }
                    break;
            }
            return true;
        }

        private void init() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            if (sharedPref.getString(SRC_KEY, "Uno").equals("Uno")) {
                togglePref(false);
            } else {
                togglePref(true);
            }

            findPreference(CAT_KEY).setSummary(sharedPref.getString(CAT_KEY, "None"));
            findPreference(SET_AS_KEY).setSummary(sharedPref.getString(SET_AS_KEY, "WallApp"));
            findPreference(SRC_KEY).setSummary(sharedPref.getString(SRC_KEY, "Uno"));
        }

        private void togglePref(boolean value) {
            findPreference(CAT_KEY).setEnabled(value);
            findPreference(BLUR_KEY).setEnabled(!value);
            findPreference(GRAY_KEY).setEnabled(!value);
        }
    }
}