package com.arnaudpiroelle.manga.data

import com.arnaudpiroelle.manga.data.core.db.dao.PageDao
import com.arnaudpiroelle.manga.data.model.Page
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PageRepository @Inject constructor(private val pageDao: PageDao) {
    fun insert(page: Page): Maybe<Long> {
        return Maybe.fromCallable { pageDao.insert(page) }
    }

}