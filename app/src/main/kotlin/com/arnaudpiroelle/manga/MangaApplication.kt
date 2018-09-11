package com.arnaudpiroelle.manga

import android.app.Application
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.provider.japscan.downloader.JapScanDownloader
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

class MangaApplication : Application() {

    @Inject
    lateinit var providerRegistry: ProviderRegistry

    @Inject
    lateinit var japScanDownloader: JapScanDownloader

    override fun onCreate() {
        super.onCreate()

        Fabric.with(this, Crashlytics())

        inject()

        providerRegistry.register("JapScan", japScanDownloader)
    }

}
