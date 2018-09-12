package com.arnaudpiroelle.manga.provider.japscan

import okhttp3.OkHttpClient
import toothpick.config.Module
import javax.inject.Inject
import javax.inject.Provider

class JapScanModule : Module() {
    init {
        bind(JapScanMangaProvider::class.java).toProvider(JapScanMangaProviderProvider::class.java)
    }
}

class JapScanMangaProviderProvider @Inject constructor(private val okHttpClient: OkHttpClient) : Provider<JapScanMangaProvider> {
    override fun get(): JapScanMangaProvider {
        return JapScanMangaProvider(okHttpClient)
    }
}