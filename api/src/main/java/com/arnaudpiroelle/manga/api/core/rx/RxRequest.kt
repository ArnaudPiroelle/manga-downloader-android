package com.arnaudpiroelle.manga.api.core.rx

import io.reactivex.Single
import io.reactivex.SingleObserver
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class RxRequest(
        private val okHttpClient: OkHttpClient,
        private val request: Request) : Single<Response>() {

    override fun subscribeActual(observer: SingleObserver<in Response>) {
        try {
            val newCall = okHttpClient.newCall(request)
            val execute = newCall.execute()
            observer.onSuccess(execute)
        } catch (e: Throwable) {
            observer.onError(e)
        }
    }

}