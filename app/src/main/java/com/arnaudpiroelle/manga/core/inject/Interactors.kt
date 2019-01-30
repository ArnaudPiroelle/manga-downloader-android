package com.arnaudpiroelle.manga.core.inject

import com.arnaudpiroelle.manga.ui.manga.add.AddMangaInteractor
import com.arnaudpiroelle.manga.ui.manga.add.GetProviderMangasInteractor
import com.arnaudpiroelle.manga.ui.manga.add.GetProvidersInteractor
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module.module

var interactors = module {
    factory { GetProvidersInteractor(get(), Dispatchers.IO) }
    factory { GetProviderMangasInteractor(Dispatchers.IO) }
    factory { AddMangaInteractor(get(), Dispatchers.IO) }
}