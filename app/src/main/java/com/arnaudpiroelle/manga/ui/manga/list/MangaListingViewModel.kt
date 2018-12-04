package com.arnaudpiroelle.manga.ui.manga.list

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.ui.core.BaseViewModel
import com.arnaudpiroelle.manga.worker.TaskManager

class MangaListingViewModel(private val mangaDao: MangaDao, private val taskManager: TaskManager) : BaseViewModel<MangaListingAction, MangaListingState>(MangaListingState()) {
    val mangas = LivePagedListBuilder(mangaDao.observeAll(), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(6)
            .build())
            .build()


    override fun handle(action: MangaListingAction) {
        when (action) {
            StartSyncAction -> startSyncJob()
            DismissNotificationAction -> dismissNotifications()
        }
    }

    private fun startSyncJob() {
        taskManager.scheduleManualCheckNewChapters()
        updateState { state -> state.copy(notificationResId = R.string.notify_sync_started) }
    }

    private fun dismissNotifications() {
        updateState { state -> state.copy(notificationResId = null) }
    }

}