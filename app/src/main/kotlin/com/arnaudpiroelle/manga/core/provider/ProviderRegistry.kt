package com.arnaudpiroelle.manga.core.provider

import java.util.*

open class ProviderRegistry {

    private var registry: MutableMap<String, MangaProvider> = HashMap()

    fun register(name: String, mangaProvider: MangaProvider) {
        registry[name] = mangaProvider
    }

    fun find(providerName: String): MangaProvider {
        return registry[providerName]!!
    }

    fun list(): Map<String, MangaProvider> {
        return registry
    }


}

class ProviderRegistryBuilder {

    private val providerRegistry: ProviderRegistry = ProviderRegistry()

    fun withProvider(name: String, mangaProvider: MangaProvider): ProviderRegistryBuilder {
        providerRegistry.register(name, mangaProvider)

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