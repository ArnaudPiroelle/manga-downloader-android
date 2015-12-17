package com.arnaudpiroelle.manga.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.arnaudpiroelle.manga.BuildConfig
import com.arnaudpiroelle.manga.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class PreferencesHelperTest {

    lateinit var sharedPrefs: SharedPreferences
    lateinit var preferencesHelper: PreferencesHelper
    lateinit var context: Context

    @Before
    fun setup() {
        context = RuntimeEnvironment.application
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        preferencesHelper = PreferencesHelper(context, sharedPrefs)
    }

    @Test
    fun should_return_true_if_update_on_wifi_only_is_not_set() {
        // Given / When
        sharedPrefs.edit().remove(context.getString(R.string.pref_wifiswitch_key)).apply()
        val updateOnWifiOnly = preferencesHelper.isUpdateOnWifiOnly

        // Then
        assertThat(updateOnWifiOnly).isTrue()

    }

    @Test
    fun should_return_false_if_update_on_wifi_only_is_set_to_false() {
        // Given / When
        sharedPrefs.edit().putBoolean(context.getString(R.string.pref_wifiswitch_key), false).apply()
        val updateOnWifiOnly = preferencesHelper.isUpdateOnWifiOnly

        // Then
        assertThat(updateOnWifiOnly).isFalse()

    }

    @Test
    fun should_return_true_if_auto_update_is_not_set() {
        // Given / When
        sharedPrefs.edit().remove(context.getString(R.string.pref_auto_update_key)).apply()
        val autoUpdate = preferencesHelper.isAutoUpdate

        // Then
        assertThat(autoUpdate).isTrue()

    }

    @Test
    fun should_return_false_if_auto_update_is_set_to_false() {
        // Given / When
        sharedPrefs.edit().putBoolean(context.getString(R.string.pref_auto_update_key), false).apply()
        val autoUpdate = preferencesHelper.isAutoUpdate

        // Then
        assertThat(autoUpdate).isFalse()

    }

    @Test
    fun should_return_true_if_compress_chapter_is_not_set() {
        // Given / When
        sharedPrefs.edit().remove(context.getString(R.string.pref_compress_chapters_key)).apply()
        val value = preferencesHelper.isCompressChapter

        // Then
        assertThat(value).isTrue()

    }

    @Test
    fun should_return_false_if_compress_chapter_is_set_to_false() {
        // Given / When
        sharedPrefs.edit().putBoolean(context.getString(R.string.pref_compress_chapters_key), false).apply()
        val value = preferencesHelper.isCompressChapter

        // Then
        assertThat(value).isFalse()

    }

    @Test
    fun should_return_default_value_if_update_interval_is_not_set() {
        // Given / When
        val defaultValue = context.getString(R.string.pref_intervalpicker_defaultvalue)

        sharedPrefs.edit().remove(context.getString(R.string.pref_compress_chapters_key)).apply()
        val value = preferencesHelper.updateInterval

        // Then
        assertThat(value).isEqualTo(defaultValue)

    }

    @Test
    fun should_return_value_if_update_interval_is_set_to_false() {
        // Given / When
        sharedPrefs.edit().putString(context.getString(R.string.pref_intervalpicker_key), "2048").apply()
        val value = preferencesHelper.updateInterval

        // Then
        assertThat(value).isEqualTo("2048")

    }
}