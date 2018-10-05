package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.room.*
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.data.model.MangaWithCover
import io.reactivex.Flowable
import io.reactivex.Maybe


@Dao
interface MangaDao {

    @Query("SELECT * FROM mangas ORDER BY name")
    fun getAll(): Flowable<List<Manga>>

    @Query("SELECT * FROM mangas where provider = :provider")
    fun getMangaForProvider(provider: String): Flowable<List<Manga>>

    @Query("SELECT * FROM mangas where id = :id")
    fun getById(id: Long): Maybe<Manga>

    @Query("SELECT * FROM mangas where alias = :alias")
    fun getByAlias(alias: String): Maybe<Manga>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(manga: Manga): Long

    @Delete
    fun delete(manga: Manga)

    @Update
    fun update(manga: Manga)
}