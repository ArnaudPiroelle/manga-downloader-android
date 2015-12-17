package com.arnaudpiroelle.manga.core.inject.module

import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.provider.mangapanda.downloader.MockMangaPandaDownloader
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class MangaPandaModule {

    @Provides
    @Singleton
    @Named("MangaPandaDownloader")
    fun provideMangaPandaDownloader(): MangaProvider {
        return MockMangaPandaDownloader()
    }

}
