package com.arnaudpiroelle.manga.ui.manga.details

import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arnaudpiroelle.manga.data.dao.ChapterDao
import com.arnaudpiroelle.manga.data.dao.MangaDao
import com.arnaudpiroelle.manga.ui.common.BaseViewModel
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsContext.Action
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsContext.Action.LoadMangaInformations
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsContext.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MangaDetailsViewModel(private val mangaDao: MangaDao, private val chapterDao: ChapterDao, private val mangaId: Long) : BaseViewModel<Action, State>(State()) {

    val chapters = LivePagedListBuilder(chapterDao.observeAll(mangaId), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(6)
            .build())
            .build()

    override suspend fun onHandle(action: Action) {
        when (action) {
            is LoadMangaInformations -> loadMangaInformations()
            is Action.RemoveMangaAction -> removeManga(action.mangaId)
        }
    }

    private suspend fun loadMangaInformations() {
        val manga = withContext(Dispatchers.IO) {
            mangaDao.getById(mangaId)
        }

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
        withContext(Dispatchers.IO) {
            mangaDao.deleteById(mangaId)
        }

        updateState { state -> state.copy(removed = true) }
    }

    private fun origin(origin: String): String {
        return when (origin) {
            "Japon" -> "${String(Character.toChars(0x1F1EF) + Character.toChars(0x1F1F5))} $origin"
            else -> origin
        }
    }
}