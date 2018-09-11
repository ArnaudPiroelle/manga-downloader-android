package com.arnaudpiroelle.manga.core.inject.provider

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Provider

class OkHttpClientProvider : Provider<OkHttpClient> {
    override fun get(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
    }

}