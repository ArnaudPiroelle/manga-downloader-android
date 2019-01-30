package com.arnaudpiroelle.manga.ui.history

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arnaudpiroelle.manga.data.dao.HistoryDao

class HistoriesViewModel(private val historyDao: HistoryDao) : ViewModel() {
    val histories = LivePagedListBuilder(historyDao.observeAll(), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(8)
            .build())
            .build()

    fun cleanHistory() {

    }
}