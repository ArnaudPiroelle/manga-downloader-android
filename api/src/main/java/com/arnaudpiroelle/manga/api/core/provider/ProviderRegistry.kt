package com.arnaudpiroelle.manga.api.core.provider

import com.arnaudpiroelle.manga.api.provider.MangaProvider
import java.util.*

class ProviderRegistry {

    private var registry: MutableMap<String, Provider> = HashMap()

    fun register(name: String, mangaProvider: MangaProvider) {
        registry[name] = Provider(name, mangaProvider)
    }

    fun find(providerName: String): MangaProvider? {
        return registry[providerName]?.mangaProvider
    }

    fun list(): Map<String, Provider> {
        return registry
    }

    data class Provider(val name: String, val mangaProvider: MangaProvider)


}