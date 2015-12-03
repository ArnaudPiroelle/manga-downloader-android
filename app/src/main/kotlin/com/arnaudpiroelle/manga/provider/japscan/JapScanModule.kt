package com.arnaudpiroelle.manga.provider.japscan

import com.arnaudpiroelle.manga.provider.japscan.api.JapScanApiService
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanDataApiService
import com.arnaudpiroelle.manga.provider.japscan.downloader.JapScanDownloader

import javax.inject.Named
import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import retrofit.RestAdapter
import retrofit.client.OkClient

import com.arnaudpiroelle.manga.BuildConfig.JAPSCAN_BASE_URL
import com.arnaudpiroelle.manga.BuildConfig.JAPSCAN_CDN_BASE_URL
import retrofit.RestAdapter.LogLevel.BASIC
import retrofit.RestAdapter.LogLevel.NONE

@Module
class JapScanModule {

    @Provides
    @Singleton
    @Named("JapScanRestAdapter")
    fun provideJapScanRestAdapter(client: OkClient): RestAdapter {
        return RestAdapter.Builder().setEndpoint(JAPSCAN_BASE_URL).setLogLevel(BASIC).setClient(client).build()
    }

    @Provides
    @Singleton
    @Named("JapScanCdnRestAdapter")
    fun provideJapScanCdnRestAdapter(client: OkClient): RestAdapter {
        return RestAdapter.Builder().setEndpoint(JAPSCAN_CDN_BASE_URL).setLogLevel(BASIC).setClient(client).build()
    }

    @Provides
    @Singleton
    fun provideJapScanDataApiService(@Named("JapScanCdnRestAdapter") restAdapter: RestAdapter): JapScanDataApiService {
        return restAdapter.create(JapScanDataApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideJapScanApiService(@Named("JapScanRestAdapter") restAdapter: RestAdapter): JapScanApiService {
        return restAdapter.create(JapScanApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideJapScanDownloader(japScanApiService: JapScanApiService, japScanDataApiService: JapScanDataApiService): JapScanDownloader {
        return JapScanDownloader(japScanApiService, japScanDataApiService)
    }
}
