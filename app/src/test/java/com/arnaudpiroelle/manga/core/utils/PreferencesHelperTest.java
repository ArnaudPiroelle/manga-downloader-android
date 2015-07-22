package com.arnaudpiroelle.manga.core.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.arnaudpiroelle.manga.BuildConfig;
import com.arnaudpiroelle.manga.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PreferencesHelperTest {

    private SharedPreferences sharedPrefs;
    private PreferencesHelper preferencesHelper;
    private Application context;

    @Before
    public void setup() {
        context = RuntimeEnvironment.application;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        preferencesHelper = new PreferencesHelper(context, sharedPrefs);
    }

    @Test
    public void should_return_true_if_update_on_wifi_only_is_not_set(){
        // Given / When
        sharedPrefs.edit().remove(context.getString(R.string.pref_wifiswitch_key)).apply();
        boolean updateOnWifiOnly = preferencesHelper.isUpdateOnWifiOnly();

        // Then
        assertThat(updateOnWifiOnly).isTrue();

    }

    @Test
    public void should_return_false_if_update_on_wifi_only_is_set_to_false(){
        // Given / When
        sharedPrefs.edit().putBoolean(context.getString(R.string.pref_wifiswitch_key), false).apply();
        boolean updateOnWifiOnly = preferencesHelper.isUpdateOnWifiOnly();

        // Then
        assertThat(updateOnWifiOnly).isFalse();

    }

    @Test
    public void should_return_true_if_auto_update_is_not_set(){
        // Given / When
        sharedPrefs.edit().remove(context.getString(R.string.pref_auto_update_key)).apply();
        boolean autoUpdate = preferencesHelper.isAutoUpdate();

        // Then
        assertThat(autoUpdate).isTrue();

    }

    @Test
    public void should_return_false_if_auto_update_is_set_to_false(){
        // Given / When
        sharedPrefs.edit().putBoolean(context.getString(R.string.pref_auto_update_key), false).apply();
        boolean autoUpdate = preferencesHelper.isAutoUpdate();

        // Then
        assertThat(autoUpdate).isFalse();

    }

    @Test
    public void should_return_true_if_compress_chapter_is_not_set(){
        // Given / When
        sharedPrefs.edit().remove(context.getString(R.string.pref_compress_chapters_key)).apply();
        boolean value = preferencesHelper.isCompressChapter();

        // Then
        assertThat(value).isTrue();

    }

    @Test
    public void should_return_false_if_compress_chapter_is_set_to_false(){
        // Given / When
        sharedPrefs.edit().putBoolean(context.getString(R.string.pref_compress_chapters_key), false).apply();
        boolean value = preferencesHelper.isCompressChapter();

        // Then
        assertThat(value).isFalse();

    }

    @Test
    public void should_return_default_value_if_update_interval_is_not_set(){
        // Given / When
        String defaultValue = context.getString(R.string.pref_intervalpicker_defaultvalue);

        sharedPrefs.edit().remove(context.getString(R.string.pref_compress_chapters_key)).apply();
        String value = preferencesHelper.getUpdateInterval();

        // Then
        assertThat(value).isEqualTo(defaultValue);

    }

    @Test
    public void should_return_value_if_update_interval_is_set_to_false(){
        // Given / When
        sharedPrefs.edit().putString(context.getString(R.string.pref_intervalpicker_key), "2048").apply();
        String value = preferencesHelper.getUpdateInterval();

        // Then
        assertThat(value).isEqualTo("2048");

    }
}