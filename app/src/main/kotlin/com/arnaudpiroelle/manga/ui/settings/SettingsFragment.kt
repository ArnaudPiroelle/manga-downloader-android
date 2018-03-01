package com.arnaudpiroelle.manga.ui.settings

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.service.DownloadService
import javax.inject.Inject

class SettingsFragment : PreferenceFragment() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private var autoUpdate: Preference? = null
    private var intervalUpdate: Preference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GRAPH.inject(this)

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
        val updateInterval = preferencesHelper.updateInterval
        val isAutoUpdate = preferencesHelper.isAutoUpdate
        val updateOnWifiOnly = preferencesHelper.isUpdateOnWifiOnly

        if (isAutoUpdate) {
            DownloadService.updateScheduling(activity, updateInterval.toLong() * 60 * 1000, updateOnWifiOnly)
        } else {
            DownloadService.cancelScheduling(activity)
        }
        return true
    }

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
