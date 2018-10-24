package com.arnaudpiroelle.manga.ui.manga.list

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao

class MangaListingViewModel(private val mangaDao: MangaDao) : ViewModel() {

    val mangas = LivePagedListBuilder(mangaDao.observeAll(), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(6)
            .build())
            .build()

}