package com.arnaudpiroelle.manga.api.core.provider

import com.arnaudpiroelle.manga.api.provider.MangaProvider
import java.util.*

object ProviderRegistry {

    private var registry: MutableMap<String, MangaProvider> = HashMap()

    fun register(name: String, mangaProvider: MangaProvider) {
        registry[name] = mangaProvider
    }

    fun find(providerName: String): MangaProvider? {
        return registry[providerName]
    }

    fun list(): Map<String, MangaProvider> {
        return registry
    }
}