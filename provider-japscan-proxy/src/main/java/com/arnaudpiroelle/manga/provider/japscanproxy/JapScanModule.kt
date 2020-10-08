package com.arnaudpiroelle.manga.provider.japscanproxy

import com.arnaudpiroelle.manga.api.plugin
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.koin.core.qualifier.named
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier


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

        val rootCertificate = HeldCertificate.Builder()
                .certificateAuthority(0)
                .build()

        val clientCertificate = HeldCertificate.Builder()
                .signedBy(rootCertificate)
                .build()

        val clientCertificates = HandshakeCertificates.Builder()
                .addTrustedCertificate(rootCertificate.certificate)
                .heldCertificate(clientCertificate)
                .build()

        OkHttpClient.Builder()
                .sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager)
                .hostnameVerifier(HostnameVerifier { hostname, session -> true })
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

    single<JapScanProxyApiService> {
        val retrofit: Retrofit = get(qualifier = JapScanContext.RetrofitQualifier)
        retrofit.create()
    }

    single {
        JapScanMangaProvider(get(), get())
    }

}