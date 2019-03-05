package com.arnaudpiroelle.manga

import android.app.Application
import com.arnaudpiroelle.manga.api.Mangas
import com.arnaudpiroelle.manga.core.inject.androidModule
import com.arnaudpiroelle.manga.core.inject.applicationModule
import com.arnaudpiroelle.manga.core.inject.viewModels
import com.arnaudpiroelle.manga.data.dataModule
import com.arnaudpiroelle.manga.interactors.interactors
import com.arnaudpiroelle.manga.provider.japscanproxy.JapScan
import com.arnaudpiroelle.manga.worker.notification.NotificationCenter
import com.arnaudpiroelle.manga.worker.workerModule
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
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
        Mangas.with(JapScan())

        startKoin(this, listOf(androidModule, applicationModule, viewModels, dataModule, workerModule, interactors))

        val notificationCenter: NotificationCenter = get()
        notificationCenter.createChannelNotification()
    }
}

@GlideModule
class MyAppGlideModule : AppGlideModule()