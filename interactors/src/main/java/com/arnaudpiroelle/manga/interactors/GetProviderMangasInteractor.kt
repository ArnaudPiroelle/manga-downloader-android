package com.arnaudpiroelle.manga.interactors

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetProviderMangasInteractor(private val dispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(provider: ProviderRegistry.Provider) = withContext(dispatcher) {
        provider.mangaProvider.findMangas()
    }
}