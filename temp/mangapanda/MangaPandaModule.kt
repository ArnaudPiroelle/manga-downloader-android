package com.arnaudpiroelle.manga.core.inject.module

import com.arnaudpiroelle.manga.BuildConfig.MANGAPANDA_BASE_URL
import com.arnaudpiroelle.manga.BuildConfig.MANGAPANDA_CDN_BASE_URL
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaApiService
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaDataApiService
import com.arnaudpiroelle.manga.provider.mangapanda.downloader.MangaPandaDownloader
import dagger.Module
import dagger.Provides
import retrofit.RestAdapter
import retrofit.RestAdapter.LogLevel.NONE
import retrofit.client.OkClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class MangaPandaModule {

    @Provides
    @Singleton
    fun providesMangaPandaApiService(client: OkClient): MangaPandaApiService {
        val restAdapter = RestAdapter.Builder().setEndpoint(MANGAPANDA_BASE_URL).setLogLevel(NONE).setClient(client).build()

        return restAdapter.create(MangaPandaApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesMangaPandaDataApiService(client: OkClient): MangaPandaDataApiService {
        val restAdapter = RestAdapter.Builder().setEndpoint(MANGAPANDA_CDN_BASE_URL).setLogLevel(NONE).setClient(client).build()

        return restAdapter.create(MangaPandaDataApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("MangaPandaDownloader")
    fun provideMangaPandaDownloader(mangaPandaApiService: MangaPandaApiService, mangaPandaDataApiService: MangaPandaDataApiService): MangaProvider {
        return MangaPandaDownloader(mangaPandaApiService, mangaPandaDataApiService)
    }
}
