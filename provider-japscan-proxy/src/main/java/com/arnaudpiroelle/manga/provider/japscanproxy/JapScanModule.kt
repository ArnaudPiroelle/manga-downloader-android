package com.arnaudpiroelle.manga.provider.japscanproxy

import com.arnaudpiroelle.manga.api.plugin
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object JapScanContext {
    val GsonQualifier = named("JapScanGson")
    val RetrofitQualifier = named("JapScanRetrofit")
    val OkHttpClientQualifer = named("JapScanOkHttpClient")
}

val japScanModule = plugin {
    single(qualifier = JapScanContext.GsonQualifier) { Gson() }

    single(qualifier = JapScanContext.OkHttpClientQualifer) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
    }

    single(qualifier = JapScanContext.RetrofitQualifier) {
        Retrofit.Builder()
                .client(get(qualifier = JapScanContext.OkHttpClientQualifer))
                .baseUrl(BuildConfig.JAPSCAN_PROXY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(get(qualifier = JapScanContext.GsonQualifier)))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
    }

    single {
        val retrofit: Retrofit = get(qualifier = JapScanContext.RetrofitQualifier)
        retrofit.create<JapScanProxyApiService>()
    }

    single {
        JapScanMangaProvider(get(), get())
    }

}