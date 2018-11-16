package com.arnaudpiroelle.manga

import android.app.Application
import com.arnaudpiroelle.manga.api.Mangas
import com.arnaudpiroelle.manga.core.inject.androidModule
import com.arnaudpiroelle.manga.core.inject.applicationModule
import com.arnaudpiroelle.manga.core.inject.databaseModule
import com.arnaudpiroelle.manga.core.inject.viewModels
import com.arnaudpiroelle.manga.core.manager.Schedulers
import com.arnaudpiroelle.manga.provider.japscan.JapScan
import com.facebook.stetho.Stetho
import org.koin.android.ext.android.get
import org.koin.android.ext.android.startKoin
import timber.log.Timber

class MangaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Timber.plant(Timber.DebugTree())
        }

        Mangas.with(this, JapScan())

        startKoin(this, listOf(androidModule, applicationModule, databaseModule, viewModels))

        val schedulers = get<Schedulers>()
        schedulers.scheduleTaskService()
        schedulers.scheduleCheckerService()

    }


}
