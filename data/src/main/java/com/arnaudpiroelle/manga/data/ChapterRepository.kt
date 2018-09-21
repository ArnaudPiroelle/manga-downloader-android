package com.arnaudpiroelle.manga.data

import com.arnaudpiroelle.manga.data.core.db.dao.ChapterDao
import com.arnaudpiroelle.manga.data.model.Chapter
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChapterRepository @Inject constructor(private val chapterDao: ChapterDao) {
    fun insert(chapter: Chapter): Maybe<Long> {
        return Maybe.fromCallable { chapterDao.insert(chapter) }
    }
}