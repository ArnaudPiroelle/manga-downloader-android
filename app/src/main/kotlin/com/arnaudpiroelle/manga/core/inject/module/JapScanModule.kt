package com.arnaudpiroelle.manga.core.inject.module

import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.provider.japscan.downloader.JapScanDownloader
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class JapScanModule {

    @Provides
    @Singleton
    @Named("JapScanDownloader")
    fun provideJapScanDownloader(okHttpClient: OkHttpClient): MangaProvider {
        return JapScanDownloader(okHttpClient)
    }
}
