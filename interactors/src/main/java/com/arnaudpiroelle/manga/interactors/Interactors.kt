package com.arnaudpiroelle.manga.interactors

import org.koin.dsl.module

var interactors = module {
    factory { GetProvidersInteractor(get()) }
    factory { GetProviderMangasInteractor() }
    factory { AddMangaInteractor(get(), get()) }
}