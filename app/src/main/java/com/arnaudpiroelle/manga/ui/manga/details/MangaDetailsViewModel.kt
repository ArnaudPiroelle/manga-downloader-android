package com.arnaudpiroelle.manga.ui.manga.details

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arnaudpiroelle.manga.data.dao.ChapterDao
import com.arnaudpiroelle.manga.data.dao.HistoryDao
import com.arnaudpiroelle.manga.data.dao.MangaDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Chapter.Status.WANTED
import com.arnaudpiroelle.manga.data.model.History
import com.arnaudpiroelle.manga.ui.common.BaseViewModel
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsContext.Action
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsContext.Action.*
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsContext.State

class MangaDetailsViewModel(private val mangaDao: MangaDao, private val chapterDao: ChapterDao, private val historyDao: HistoryDao, private val mangaId: Long) : BaseViewModel<Action, State>(State()) {

    val chapters = LivePagedListBuilder(chapterDao.observeAll(mangaId), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(6)
            .build())
            .build()

    override suspend fun onHandle(action: Action) {
        when (action) {
            is LoadMangaInformations -> loadMangaInformations()
            is RemoveMangaAction -> removeManga(action.mangaId)
            is ChangeChapterStatusAction -> changeChapterStatus(action.item)
            is ChangeAllChaptersStatusAction -> changeAllChaptersStatus()
        }
    }

    private suspend fun changeAllChaptersStatus() {
        chapterDao.setAllChaptersStatusAs(mangaId, WANTED)
    }

    private suspend fun changeChapterStatus(item: Chapter) {
        chapterDao.update(item.copy(status = WANTED))
    }

    private suspend fun loadMangaInformations() {
        val manga = mangaDao.getById(mangaId)

        if (manga != null) {
            updateState { state ->
                state.copy(
                        title = manga.name,
                        thumbnail = manga.thumbnail,
                        origin = origin(manga.origin),
                        year = manga.year,
                        type = manga.type,
                        author = manga.author,
                        summary = manga.summary
                )
            }
        }
    }

    private suspend fun removeManga(mangaId: Long) {
        val manga = mangaDao.getById(mangaId)
        manga?.let {
            historyDao.insert(History(
                    label = it.name,
                    sublabel = "Removed"
            ))
        }

        mangaDao.deleteById(mangaId)

        updateState { state -> state.copy(removed = true) }
    }

    private fun origin(origin: String): String {
        return when (origin) {
            "Japon" -> "${String(Character.toChars(0x1F1EF) + Character.toChars(0x1F1F5))} $origin"
            else -> origin
        }
    }
}