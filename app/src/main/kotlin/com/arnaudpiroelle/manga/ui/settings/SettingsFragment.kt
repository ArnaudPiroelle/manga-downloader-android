package com.arnaudpiroelle.manga.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.service.DownloadService
import com.arnaudpiroelle.manga.ui.explorer.ExplorerActivity
import kotlinx.android.synthetic.main.fragment_preferences.*
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {
    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private var autoUpdate: Preference? = null
    private var intervalUpdate: Preference? = null
    private var outputFolder: Preference? = null

    override fun onResume() {
        super.onResume()

        toolbar.title = "Settings"
    }

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preferences)

        inject()

        setupPreferences()
    }

    private fun setupPreferences() {
        autoUpdate = findPreference(getString(R.string.pref_auto_update_key))
        intervalUpdate = findPreference(getString(R.string.pref_intervalpicker_key))
        outputFolder = findPreference(getString(R.string.pref_output_folder_key))

        outputFolder?.apply {
            summary = preferencesHelper.getOutputFolder() ?: "Choose a folder"
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val explorerActivity = activity as? ExplorerActivity
                explorerActivity?.selectDestinationFolder()
                true
            }

            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _: Preference, _: Any ->
                //FIXME: Not called :(
                summary = preferencesHelper.getOutputFolder() ?: "Choose a folder"
                true
            }
        }

        autoUpdate?.onPreferenceChangeListener = onSchedulerPreferenceChangeListener()
        intervalUpdate?.onPreferenceChangeListener = onSchedulerPreferenceChangeListener()
    }

    private fun onSchedulerPreferenceChangeListener() = Preference.OnPreferenceChangeListener { _: Preference, _: Any ->
        //DownloadService.updateScheduling(requireActivity(), preferencesHelper)
        true
    }

}
