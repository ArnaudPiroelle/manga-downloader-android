package com.arnaudpiroelle.manga.ui.manga.add

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetProvidersInteractor(private val providerRegistry: ProviderRegistry, private val dispatcher: CoroutineDispatcher) {
    suspend operator fun invoke() = withContext(dispatcher) {
        providerRegistry.list().map { ProviderSpinnerAdapter.Provider(it.key, it.value) }
    }
}