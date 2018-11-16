package com.arnaudpiroelle.manga.core.inject

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.SearchManager
import android.app.job.JobScheduler
import android.content.Context
import android.net.ConnectivityManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val androidModule = module {
    single { androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    single { androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    single { androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    single { androidContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager }
    single { androidContext().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler }

}