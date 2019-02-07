package com.arnaudpiroelle.manga.provider.mock

import com.arnaudpiroelle.manga.api.Plugin
import okhttp3.OkHttpClient

class Mock : Plugin {
    override fun getName() = "Local"

    override fun getProvider(okHttpClient: OkHttpClient) = MockMangaProvider(okHttpClient)
}