package com.arnaudpiroelle.manga.ui.manga.list

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.data.dao.MangaDao
import com.arnaudpiroelle.manga.ui.common.BaseViewModel
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingContext.Action
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingContext.Action.DismissNotificationAction
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingContext.Action.StartSyncAction
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingContext.State
import com.arnaudpiroelle.manga.worker.TaskManager

class MangaListingViewModel(private val mangaDao: MangaDao, private val taskManager: TaskManager) : BaseViewModel<Action, State>(State()) {
    val mangas = LivePagedListBuilder(mangaDao.observeAll(), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(6)
            .build())
            .build()


    override suspend fun onHandle(action: Action) {
        when (action) {
            StartSyncAction -> startSyncJob()
            DismissNotificationAction -> dismissNotifications()
        }
    }

    private fun startSyncJob() {
        taskManager.schedulePeriodicCheckNewChapters()
        updateState { state -> state.copy(notificationResId = R.string.notify_sync_started) }
    }

    private fun dismissNotifications() {
        updateState { state -> state.copy(notificationResId = null) }
    }

}