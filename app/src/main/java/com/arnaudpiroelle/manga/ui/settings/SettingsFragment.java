package com.arnaudpiroelle.manga.ui.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.service.DownloadService;

import java.util.Objects;

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

        autoUpdate.setOnPreferenceChangeListener(this::onSchedulerPreferenceChangeListener);
        intervalUpdate.setOnPreferenceChangeListener(this::onSchedulerPreferenceChangeListener);

    }

    private boolean onSchedulerPreferenceChangeListener(Preference preference, Object newValue) {
        DownloadService.updateScheduling(getActivity());
        return true;
    }
}
