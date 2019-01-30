package com.arnaudpiroelle.manga.ui.manga.add

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetProviderMangasInteractor(private val dispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(provider: ProviderSpinnerAdapter.Provider) = withContext(dispatcher) {
        provider.mangaProvider.findMangas()
    }
}