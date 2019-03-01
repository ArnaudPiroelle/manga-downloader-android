package com.arnaudpiroelle.manga.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.setActionBar
import com.arnaudpiroelle.manga.worker.utils.PreferencesHelper
import kotlinx.android.synthetic.main.include_bottombar.*
import org.koin.android.ext.android.inject

class SettingsFragment : PreferenceFragmentCompat() {
    private val preferencesHelper: PreferencesHelper by inject()

    override fun onResume() {
        super.onResume()

        //title.setText(R.string.title_settings)
        setActionBar(bar)
    }

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onBindPreferences() {
        listView?.isNestedScrollingEnabled = false

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
