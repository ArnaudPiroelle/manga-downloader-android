package com.arnaudpiroelle.manga.core.inject

import com.arnaudpiroelle.manga.ui.history.HistoriesViewModel
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaViewModel
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModels = module(createOnStart = true) {
    viewModel { MangaListingViewModel(get(), get()) }
    viewModel { AddMangaViewModel(get(), get(), get(), get()) }
    viewModel { HistoriesViewModel(get()) }
}