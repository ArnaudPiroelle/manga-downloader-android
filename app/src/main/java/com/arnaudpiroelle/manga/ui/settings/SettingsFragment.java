package com.arnaudpiroelle.manga.ui.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.service.DownloadService;

public class SettingsFragment extends PreferenceFragment {

    private Preference intervalUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        setupPreferences();
    }

    private void setupPreferences() {
        intervalUpdate = findPreference(getString(R.string.pref_intervalpicker_key));
        intervalUpdate.setOnPreferenceChangeListener((preference, newValue) -> {
            updateServiceScheduler();
            return true;
        });
    }

    private void updateServiceScheduler() {
        DownloadService.updateScheduling(getActivity());
    }
}
