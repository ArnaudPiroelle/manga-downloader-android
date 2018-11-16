package com.arnaudpiroelle.manga.ui.manga.list

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.ui.core.BaseViewModel
import com.arnaudpiroelle.manga.R

class MangaListingViewModel(private val mangaDao: MangaDao) : BaseViewModel<MangaListingAction, MangaListingState>(MangaListingState()) {
    val mangas = LivePagedListBuilder(mangaDao.observeAll(), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(6)
            .build())
            .build()


    override fun handle(action: MangaListingAction) {
        when (action) {
            StartSync -> startSyncJob()
            DismissNotification -> dismissNotifications()
        }
    }

    private fun startSyncJob() {
        //TODO: Start job scheduler

        updateState { state -> state.copy(notificationResId = R.string.notify_sync_started) }
    }

    private fun dismissNotifications() {
        updateState { state -> state.copy(notificationResId = null) }
    }

}