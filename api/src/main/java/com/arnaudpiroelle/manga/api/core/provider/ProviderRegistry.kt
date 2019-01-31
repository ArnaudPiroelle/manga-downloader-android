package com.arnaudpiroelle.manga.api.core.provider

import com.arnaudpiroelle.manga.api.provider.MangaProvider
import java.util.*

object ProviderRegistry {

    private var registry: MutableMap<String, Provider> = HashMap()

    fun register(name: String, mangaProvider: MangaProvider) {
        registry[name] = ProviderRegistry.Provider(name, mangaProvider)
    }

    fun find(providerName: String): MangaProvider? {
        return registry[providerName]?.mangaProvider
    }

    fun list(): Map<String, Provider> {
        return registry
    }

    data class Provider(val name: String, val mangaProvider: MangaProvider)
}