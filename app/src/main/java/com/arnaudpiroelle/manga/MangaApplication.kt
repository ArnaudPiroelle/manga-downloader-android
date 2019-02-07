package com.arnaudpiroelle.manga

import android.app.Application
import com.arnaudpiroelle.manga.api.Mangas
import com.arnaudpiroelle.manga.core.inject.androidModule
import com.arnaudpiroelle.manga.core.inject.applicationModule
import com.arnaudpiroelle.manga.core.inject.viewModels
import com.arnaudpiroelle.manga.data.dataModule
import com.arnaudpiroelle.manga.interactors.interactors
import com.arnaudpiroelle.manga.provider.japscan.JapScan
import com.arnaudpiroelle.manga.provider.mock.Mock
import com.arnaudpiroelle.manga.worker.TaskManager
import com.arnaudpiroelle.manga.worker.notification.NotificationCenter
import com.arnaudpiroelle.manga.worker.workerModule
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import org.koin.android.ext.android.startKoin
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber

class MangaApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Timber.plant(Timber.DebugTree())
        }

        LeakCanary.install(this)
        Mangas.with(Mock(), JapScan())

        startKoin(this, listOf(androidModule, applicationModule, viewModels, dataModule, workerModule, interactors))

        val taskManager: TaskManager = get()
        val notificationCenter: NotificationCenter = get()

        taskManager.schedulePeriodicCheckNewChapters()
        notificationCenter.createChannelNotification()
    }
}
