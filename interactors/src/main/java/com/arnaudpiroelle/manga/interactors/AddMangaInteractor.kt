package com.arnaudpiroelle.manga.interactors

import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.data.dao.HistoryDao
import com.arnaudpiroelle.manga.data.dao.MangaDao
import com.arnaudpiroelle.manga.data.model.History
import com.arnaudpiroelle.manga.data.model.Manga.Status.ADDED

class AddMangaInteractor(private val mangaDao: MangaDao, private val historyDao: HistoryDao) {
    suspend operator fun invoke(manga: Manga): Long {
        val mangaId = mangaDao.insert(com.arnaudpiroelle.manga.data.model.Manga(
                name = manga.name,
                alias = manga.alias,
                provider = manga.provider,
                thumbnail = "",
                status = ADDED)

        )

        historyDao.insert(History(
                label = manga.name,
                sublabel = "Added"
        ))

        return mangaId
    }
}