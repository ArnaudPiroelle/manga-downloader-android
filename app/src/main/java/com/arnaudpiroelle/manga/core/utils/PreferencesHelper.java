package com.arnaudpiroelle.manga.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.arnaudpiroelle.manga.R;

import javax.inject.Inject;

public class PreferencesHelper {

    private SharedPreferences sharedPreferences;
    private Context context;

    @Inject
    public PreferencesHelper(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    public boolean isUpdateOnWifiOnly(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_wifiswitch_key), true);
    }

    public boolean isAutoUpdate(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_auto_update_key), true);
    }

    public boolean isCompressChapter(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_compress_chapters_key), true);
    }

    public String getUpdateInterval() {
        String key = context.getString(R.string.pref_intervalpicker_key);
        String defaultValue = context.getString(R.string.pref_intervalpicker_defaultvalue);
        return sharedPreferences.getString(key, defaultValue);
    }
}
