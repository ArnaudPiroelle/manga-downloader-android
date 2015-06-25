package com.arnaudpiroelle.manga.ui.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.service.DownloadService;

public class SettingsFragment extends PreferenceFragment {

    private Preference autoUpdate;

    private Preference intervalUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setupPreferences();
    }

    private void setupPreferences() {
        autoUpdate = findPreference(getString(R.string.pref_auto_update_key));
        intervalUpdate = findPreference(getString(R.string.pref_intervalpicker_key));

        autoUpdate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return onSchedulerPreferenceChangeListener(preference, newValue);
            }
        });
        intervalUpdate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return onSchedulerPreferenceChangeListener(preference, newValue);
            }
        });

    }

    private boolean onSchedulerPreferenceChangeListener(Preference preference, Object newValue) {
        DownloadService.updateScheduling(getActivity());
        return true;
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }
}
