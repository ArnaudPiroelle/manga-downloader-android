package com.arnaudpiroelle.manga.core.inject.module

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import toothpick.config.Module

class ApplicationModule(context: Context) : Module() {

    init {
        bind(Context::class.java).toInstance(context)
        bind(NotificationManager::class.java).toInstance(provideNotificationManager(context))
        bind(ConnectivityManager::class.java).toInstance(provideConnectivityManager(context))
        bind(AlarmManager::class.java).toInstance(providesAlarmManager(context))
        bind(SharedPreferences::class.java).toInstance(provideDefaultSharedPreferences(context))
    }

    private fun provideNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private fun providesAlarmManager(context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun provideDefaultSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

}
