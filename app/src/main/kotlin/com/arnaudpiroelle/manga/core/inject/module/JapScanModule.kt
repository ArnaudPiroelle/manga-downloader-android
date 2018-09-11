package com.arnaudpiroelle.manga.core.inject.module

import com.arnaudpiroelle.manga.core.inject.provider.JapScanDownloaderProvider
import com.arnaudpiroelle.manga.provider.japscan.downloader.JapScanDownloader
import toothpick.config.Module

class JapScanModule : Module() {

    init {
        bind(JapScanDownloader::class.java).toProvider(JapScanDownloaderProvider::class.java)
    }
}
