package com.arnaudpiroelle.manga.core.inject.module;

import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {
    @Provides
    SharedPreferences provideDefaultSharedPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    PackageManager providePackageManager(Context context){
        return context.getPackageManager();
    }

    @Provides
    PackageInfo providePackageInfo(PackageManager packageManager, Context context) {
        try {
            return packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    TelephonyManager provideTelephonyManager(Context context) {
        return getSystemService(context, Context.TELEPHONY_SERVICE);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSystemService(Context context, String serviceConstant) {
        return (T) context.getSystemService(serviceConstant);
    }

    @Provides
    InputMethodManager provideInputMethodManager(final Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Provides
    ApplicationInfo provideApplicationInfo(final Context context) {
        return context.getApplicationInfo();
    }

    @Provides
    AccountManager provideAccountManager(final Context context) {
        return AccountManager.get(context);
    }

    @Provides
    ClassLoader provideClassLoader(final Context context) {
        return context.getClassLoader();
    }

    @Provides
    NotificationManager provideNotificationManager(final Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    ConnectivityManager provideConnectivityManager(final Context context){
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    WifiManager providesWifiManager(final Context context){
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Provides
    AlarmManager providesAlarmManager(final Context context){
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
}
