package com.arnaudpiroelle.manga.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.arnaudpiroelle.manga.R
import javax.inject.Inject

class PreferencesHelper
@Inject
constructor(private val context: Context, private val sharedPreferences: SharedPreferences) {

    val isUpdateOnWifiOnly: Boolean
        get() = sharedPreferences.getBoolean(context.getString(R.string.pref_wifiswitch_key), true)

    val isAutoUpdate: Boolean
        get() = sharedPreferences.getBoolean(context.getString(R.string.pref_auto_update_key), true)

    fun setOutputFolder(folder: String) {
        sharedPreferences.edit().putString(context.getString(R.string.pref_output_folder_key), folder).apply()
    }

    fun getOutputFolder(): String? {
        return sharedPreferences.getString(context.getString(R.string.pref_output_folder_key), null)
    }

    val isCompressChapter: Boolean
        get() = sharedPreferences.getBoolean(context.getString(R.string.pref_compress_chapters_key), true)

    val updateInterval: String
        get() {
            val key = context.getString(R.string.pref_intervalpicker_key)
            val defaultValue = context.getString(R.string.pref_intervalpicker_defaultvalue)
            return sharedPreferences.getString(key, defaultValue)
        }
}
