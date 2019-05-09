package com.arnaudpiroelle.manga.interactors

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

var interactors = module {
    factory { GetProvidersInteractor(get(), Dispatchers.IO) }
    factory { GetProviderMangasInteractor(Dispatchers.IO) }
    factory { AddMangaInteractor(get(), get(), Dispatchers.IO) }
}