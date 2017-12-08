package com.arnaudpiroelle.manga.core.provider

import java.util.*

open class ProviderRegistry {

    var registry: MutableMap<String, MangaProvider> = HashMap()

    fun register(mangaProvider: MangaProvider) {
        registry.put(mangaProvider.name, mangaProvider)
    }

    fun find(providerName: String): MangaProvider? {
        return registry[providerName]
    }

    fun list(): List<MangaProvider> {
        return ArrayList(registry.values.sortedBy { it.name })
    }


}

class ProviderRegistryBuilder {

    private val providerRegistry: ProviderRegistry

    init {
        providerRegistry = ProviderRegistry()
    }

    fun withProvider(vararg mangaProviders: MangaProvider): ProviderRegistryBuilder {
        for (mangaProvider in mangaProviders) {
            providerRegistry.register(mangaProvider)
        }

        return this
    }

    fun build(): ProviderRegistry {
        return providerRegistry
    }

    companion object {

        fun createProviderRegister(): ProviderRegistryBuilder {
            return ProviderRegistryBuilder()
        }
    }

}