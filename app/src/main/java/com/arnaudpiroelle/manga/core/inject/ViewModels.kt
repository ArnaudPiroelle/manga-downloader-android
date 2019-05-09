package com.arnaudpiroelle.manga.core.inject

import com.arnaudpiroelle.manga.ui.history.HistoriesViewModel
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaViewModel
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsViewModel
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModels = module {
    viewModel { MangaListingViewModel(get(), get()) }
    viewModel { AddMangaViewModel(get(), get(), get(), get()) }
    viewModel { HistoriesViewModel(get()) }
    viewModel { (mangaId: Long) -> MangaDetailsViewModel(get(), get(), get(), mangaId) }
}