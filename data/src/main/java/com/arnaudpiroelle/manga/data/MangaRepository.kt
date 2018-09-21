package com.arnaudpiroelle.manga.data

import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.model.Manga
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepository @Inject constructor(private val mangaDao: MangaDao) {
    fun getAll(): Flowable<List<Manga>> {
        return mangaDao.getAll()
    }

    fun getMangaForProvider(provider: String): Flowable<List<Manga>> {
        return mangaDao.getMangaForProvider(provider)
    }

    fun getById(id: Long): Maybe<Manga> {
        return mangaDao.getById(id)
    }

    fun getByAlias(alias: String): Maybe<Manga> {
        return mangaDao.getByAlias(alias)
    }

    fun insert(manga: Manga): Maybe<Long> {
        return Maybe.fromCallable { mangaDao.insert(manga) }
    }

    fun delete(manga: Manga): Completable {
        return Completable.fromCallable { mangaDao.delete(manga) }
    }

    fun update(manga: Manga): Completable {
        return Completable.fromCallable { mangaDao.update(manga) }
    }
}