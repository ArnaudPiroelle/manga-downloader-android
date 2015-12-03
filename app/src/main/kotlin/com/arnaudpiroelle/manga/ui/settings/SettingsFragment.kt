package com.arnaudpiroelle.manga.ui.settings

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment

import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.service.DownloadService

class SettingsFragment : PreferenceFragment() {

    private var autoUpdate: Preference? = null

    private var intervalUpdate: Preference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)

        setupPreferences()
    }

    private fun setupPreferences() {
        autoUpdate = findPreference(getString(R.string.pref_auto_update_key))
        intervalUpdate = findPreference(getString(R.string.pref_intervalpicker_key))

        autoUpdate!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue -> this.onSchedulerPreferenceChangeListener(preference, newValue) }
        intervalUpdate!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue -> this.onSchedulerPreferenceChangeListener(preference, newValue) }
    }

    private fun onSchedulerPreferenceChangeListener(preference: Preference, newValue: Any): Boolean {
        DownloadService.updateScheduling(activity)
        return true
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
