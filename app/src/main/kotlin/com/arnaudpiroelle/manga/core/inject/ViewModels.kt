package com.arnaudpiroelle.manga.core.inject

import com.arnaudpiroelle.manga.ui.history.HistoriesViewModel
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaViewModel
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingViewModel
import com.arnaudpiroelle.manga.ui.tasks.TasksViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModels = module {
    viewModel { MangaListingViewModel(get()) }
    viewModel { AddMangaViewModel(get(), get(), get()) }
    viewModel { TasksViewModel(get()) }
    viewModel { HistoriesViewModel(get()) }
}