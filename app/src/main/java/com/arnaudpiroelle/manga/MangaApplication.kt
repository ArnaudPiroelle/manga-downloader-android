package com.arnaudpiroelle.manga

import android.app.Application
import com.arnaudpiroelle.manga.api.registryModule
import com.arnaudpiroelle.manga.core.inject.androidModule
import com.arnaudpiroelle.manga.core.inject.applicationModule
import com.arnaudpiroelle.manga.core.inject.viewModels
import com.arnaudpiroelle.manga.data.dao.ChapterDao
import com.arnaudpiroelle.manga.data.dataModule
import com.arnaudpiroelle.manga.data.model.Chapter.Status.DOWNLOADING
import com.arnaudpiroelle.manga.data.model.Chapter.Status.WANTED
import com.arnaudpiroelle.manga.interactors.interactors
import com.arnaudpiroelle.manga.provider.japscanproxy.japScanModule
import com.arnaudpiroelle.manga.worker.notification.NotificationCenter
import com.arnaudpiroelle.manga.worker.workerModule
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.facebook.stetho.Stetho
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import timber.log.Timber


class MangaApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@MangaApplication)
            androidFileProperties()
            modules(
                    listOf(
                            androidModule,
                            applicationModule,
                            viewModels,
                            dataModule,
                            workerModule,
                            interactors,
                            registryModule,
                            japScanModule
                    )
            )
        }

        val notificationCenter: NotificationCenter = get()
        notificationCenter.createChannelNotification()

        val chapterDao: ChapterDao = get()
        GlobalScope.launch {
            Timber.d("Reset downloading status at start")
            val chapters = chapterDao.getByStatus(DOWNLOADING)
            val wantedChapters = chapters.map { it.copy(status = WANTED) }
            chapterDao.updateAll(wantedChapters)
        }
    }
}

@GlideModule
class MyAppGlideModule : AppGlideModule()