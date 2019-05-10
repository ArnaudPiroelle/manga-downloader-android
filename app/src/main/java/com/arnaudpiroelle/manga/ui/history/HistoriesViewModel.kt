package com.arnaudpiroelle.manga.ui.history

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.data.dao.HistoryDao
import com.arnaudpiroelle.manga.ui.common.BaseViewModel
import com.arnaudpiroelle.manga.ui.history.HistoryContext.*
import com.arnaudpiroelle.manga.ui.history.HistoryContext.Action.CleanHistoryAction

class HistoriesViewModel(private val historyDao: HistoryDao, initialState: State = State()) : BaseViewModel<Action, State>(initialState) {

    val histories = LivePagedListBuilder(historyDao.observeAll(), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(8)
            .build())
            .build()

    override suspend fun onHandle(action: Action) {
        when (action) {
            CleanHistoryAction -> cleanHistory()
        }
    }

    private suspend fun cleanHistory() {
        historyDao.deleteAll()

        updateState { state -> state.copy(visualNotification = VisualNotification(R.string.history_cleared)) }
    }
}