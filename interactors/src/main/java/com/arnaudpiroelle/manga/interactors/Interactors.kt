package com.arnaudpiroelle.manga.interactors

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module.module

var interactors = module {
    factory { com.arnaudpiroelle.manga.interactors.GetProvidersInteractor(get(), Dispatchers.IO) }
    factory { com.arnaudpiroelle.manga.interactors.GetProviderMangasInteractor(Dispatchers.IO) }
    factory { com.arnaudpiroelle.manga.interactors.AddMangaInteractor(get(), Dispatchers.IO) }
}