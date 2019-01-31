package com.arnaudpiroelle.manga.interactors

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetProvidersInteractor(private val providerRegistry: ProviderRegistry, private val dispatcher: CoroutineDispatcher) {
    suspend operator fun invoke() = withContext(dispatcher) {
        providerRegistry.list().values.toList()
    }
}