package com.arnaudpiroelle.manga

import android.app.Application
import com.arnaudpiroelle.manga.api.Mangas
import com.arnaudpiroelle.manga.core.inject.androidModule
import com.arnaudpiroelle.manga.core.inject.applicationModule
import com.arnaudpiroelle.manga.core.inject.interactors
import com.arnaudpiroelle.manga.core.inject.viewModels
import com.arnaudpiroelle.manga.provider.japscan.JapScan
import com.arnaudpiroelle.manga.worker.TaskManager
import com.arnaudpiroelle.manga.worker.notification.NotificationCenter
import com.arnaudpiroelle.manga.worker.workerModule
import com.facebook.stetho.Stetho
import org.koin.android.ext.android.startKoin
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber

class MangaApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Timber.plant(Timber.DebugTree())
        }

        Mangas.with(JapScan())

        startKoin(this, listOf(androidModule, applicationModule, viewModels, com.arnaudpiroelle.manga.data.dataModule, workerModule, interactors))

        val taskManager: TaskManager = get()
        val notificationCenter: NotificationCenter = get()

        taskManager.schedulePeriodicCheckNewChapters()
        notificationCenter.createChannelNotification()
    }
}
