package com.arnaudpiroelle.manga.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.core.utils.setActionBar
import kotlinx.android.synthetic.main.include_bottombar.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.android.ext.android.inject

class SettingsFragment : PreferenceFragmentCompat() {
    private val preferencesHelper: PreferencesHelper by inject()

    private var autoUpdate: Preference? = null
    private var intervalUpdate: Preference? = null
    private var outputFolder: Preference? = null

    override fun onResume() {
        super.onResume()

        title.setText(R.string.title_settings)
        setActionBar(bar)
    }

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preferences)
        setupPreferences()
    }

    private fun setupPreferences() {
        autoUpdate = findPreference(getString(R.string.pref_auto_update_key))
        intervalUpdate = findPreference(getString(R.string.pref_intervalpicker_key))
        outputFolder = findPreference(getString(R.string.pref_output_folder_key))

        outputFolder?.apply {
            summary = preferencesHelper.getOutputFolder() ?: "Choose a folder"
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
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
