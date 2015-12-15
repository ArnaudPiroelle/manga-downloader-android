package com.arnaudpiroelle.manga.core.inject.module

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.provider.ProviderRegistryBuilder.Companion.createProviderRegister
import com.arnaudpiroelle.manga.provider.japscan.downloader.JapScanDownloader
import com.arnaudpiroelle.manga.provider.mangapanda.downloader.MangaPandaDownloader
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.okhttp.OkHttpClient
import dagger.Module
import dagger.Provides
import retrofit.client.OkClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class ApplicationModule(private val mContext: Context) {

    @Provides
    @Singleton
    fun providesContext(): Context {
        return mContext
    }

    @Provides
    @Singleton
    fun providesHttpClient(): OkClient {
        val client = OkHttpClient()
        client.setReadTimeout(60, TimeUnit.SECONDS)
        client.setConnectTimeout(60, TimeUnit.SECONDS)

        return OkClient(client)
    }

    @Provides
    @Singleton
    fun providesProviderRegistry(japScanDownloader: JapScanDownloader, mangaPandaDownloader: MangaPandaDownloader): ProviderRegistry {
        return createProviderRegister().withProvider(japScanDownloader, mangaPandaDownloader).build()
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder().setPrettyPrinting().create()
    }

    @Provides
    fun provideNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun providesAlarmManager(context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    fun provideDefaultSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

}
