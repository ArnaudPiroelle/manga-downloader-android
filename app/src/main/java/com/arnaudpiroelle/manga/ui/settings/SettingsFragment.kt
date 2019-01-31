package com.arnaudpiroelle.manga.ui.settings

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.setActionBar
import com.arnaudpiroelle.manga.worker.utils.PreferencesHelper
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

    override fun onBindPreferences() {
        listView?.isNestedScrollingEnabled = false

        listView?.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                checkScroll()
            }

            override fun onChildViewAttachedToWindow(view: View) {
                checkScroll()
            }
        })

        val adapter = listView?.adapter
        adapter?.notifyDataSetChanged()
    }

    private fun checkScroll() {
        val adapter = listView?.adapter
        val layoutManager = listView.layoutManager as LinearLayoutManager
        val totalItems = adapter?.itemCount ?: 0
        val findLastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

        listView.isNestedScrollingEnabled = findLastCompletelyVisibleItemPosition != totalItems - 1
    }

    private fun onSchedulerPreferenceChangeListener() = Preference.OnPreferenceChangeListener { _: Preference, _: Any ->
        //DownloadService.updateScheduling(requireActivity(), preferencesHelper)
        true
    }

}
