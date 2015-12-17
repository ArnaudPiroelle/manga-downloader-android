package com.arnaudpiroelle.manga.core.inject.module

import android.content.Context
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.provider.japscan.downloader.MockJapScanDownloader
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class JapScanModule {

    @Provides
    @Singleton
    @Named("JapScanDownloader")
    fun provideJapScanDownloader(context: Context): MangaProvider {
        return MockJapScanDownloader(context)
    }
}
