package com.arnaudpiroelle.manga.core.inject

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.service.task.DownloadChapterTaskExecution
import com.arnaudpiroelle.manga.service.task.RetrieveChaptersTaskExecution
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val applicationModule = module {
    single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    single { androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    single { androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    single { androidContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager }

    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single { PreferencesHelper(androidContext(), get()) }
    single { RetrieveChaptersTaskExecution(get(), get(), get(), get()) }
    single { DownloadChapterTaskExecution() }
    single { ProviderRegistry }
}