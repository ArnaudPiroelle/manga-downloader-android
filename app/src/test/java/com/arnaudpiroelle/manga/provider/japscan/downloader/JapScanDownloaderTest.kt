package com.arnaudpiroelle.manga.provider.japscan.downloader

import com.arnaudpiroelle.manga.core.rx.RxRequest
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test

class JapScanDownloaderTest {

    @Test
    fun name() {
        Single.error<List<String>>(Exception())
                .flatMapObservable { Observable.fromIterable(it) }
                .subscribe({ println(it) }, {
                    println("hey")
                    it.printStackTrace()
                })

        Thread.sleep(2000)

    }

    @Test
    fun shouldDownloadAndProcessPage() {

    }
}