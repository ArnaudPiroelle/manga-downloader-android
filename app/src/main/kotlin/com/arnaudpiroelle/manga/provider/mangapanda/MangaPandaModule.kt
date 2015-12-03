package com.arnaudpiroelle.manga.provider.mangapanda

import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaApiService
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaDataApiService

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import retrofit.RestAdapter
import retrofit.client.OkClient

import com.arnaudpiroelle.manga.BuildConfig.MANGAPANDA_BASE_URL
import com.arnaudpiroelle.manga.BuildConfig.MANGAPANDA_CDN_BASE_URL
import retrofit.RestAdapter.LogLevel.NONE

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
}
