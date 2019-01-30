package com.arnaudpiroelle.manga.api

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object Mangas {
    fun with(vararg plugins: Plugin) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

        plugins.forEach {
            val providerName = it.getName()
            val provider = it.getProvider(okHttpClient)

            ProviderRegistry.register(providerName, provider)
        }
    }
}

interface Plugin {
    fun getName(): String
    fun getProvider(okHttpClient: OkHttpClient): MangaProvider
}