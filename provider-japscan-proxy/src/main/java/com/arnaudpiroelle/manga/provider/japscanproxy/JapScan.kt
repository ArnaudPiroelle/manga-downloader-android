package com.arnaudpiroelle.manga.provider.japscanproxy

import com.arnaudpiroelle.manga.api.Plugin
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import okhttp3.OkHttpClient

class JapScan : Plugin {

    override fun getName(): String {
        return "JapScan"
    }

    override fun getProvider(okHttpClient: OkHttpClient): MangaProvider {
        return JapScanMangaProvider(okHttpClient)
    }

}