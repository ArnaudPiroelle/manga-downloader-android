package com.arnaudpiroelle.manga.interactors

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.Manga

class GetProviderMangasInteractor {
    suspend operator fun invoke(provider: ProviderRegistry.Provider): List<Manga> {
        return provider.mangaProvider.findMangas()
    }
}