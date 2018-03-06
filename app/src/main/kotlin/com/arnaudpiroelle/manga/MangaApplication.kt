package com.arnaudpiroelle.manga

import android.app.Application
import com.arnaudpiroelle.manga.core.inject.DaggerMangaComponent
import com.arnaudpiroelle.manga.core.inject.MangaComponent
import com.arnaudpiroelle.manga.core.inject.module.ApplicationModule
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class MangaApplication : Application() {

    companion object {
        lateinit var GRAPH: MangaComponent
    }

    override fun onCreate() {
        super.onCreate()

        Fabric.with(this, Crashlytics())

        GRAPH = DaggerMangaComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()

    }

}
