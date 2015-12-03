package com.arnaudpiroelle.manga.core.provider

import java.util.*

class ProviderRegistry {

    internal var registry: MutableMap<String, MangaProvider>

    init {
        registry = HashMap<String, MangaProvider>()
    }

    private fun register(mangaProvider: MangaProvider) {
        registry.put(mangaProvider.name, mangaProvider)
    }

    operator fun get(providerName: String): MangaProvider? {
        return registry[providerName]
    }

    fun list(): List<MangaProvider> {
        return ArrayList(registry.values.sortedBy { it.name })
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
}
