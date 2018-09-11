package com.arnaudpiroelle.manga.core.inject.provider

import com.arnaudpiroelle.manga.provider.japscan.downloader.JapScanDownloader
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Provider

class JapScanDownloaderProvider @Inject constructor(val okHttpClient: OkHttpClient) : Provider<JapScanDownloader> {
    override fun get(): JapScanDownloader {
        return JapScanDownloader(okHttpClient)
    }
}