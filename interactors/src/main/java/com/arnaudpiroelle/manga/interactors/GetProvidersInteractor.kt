package com.arnaudpiroelle.manga.interactors

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry

class GetProvidersInteractor(private val providerRegistry: ProviderRegistry) {
    operator fun invoke(): List<ProviderRegistry.Provider> {
        return providerRegistry.list().values.toList()
    }
}