package com.arnaudpiroelle.manga.core.db.dao

import androidx.room.*
import com.arnaudpiroelle.manga.model.db.Manga
import io.reactivex.Flowable
import io.reactivex.Maybe


@Dao
interface MangaDao {
    @Query("SELECT * FROM manga")
    fun getAll(): Flowable<List<Manga>>

    @Query("SELECT * FROM manga where provider = :provider")
    fun getMangaForProvider(provider: String): Flowable<List<Manga>>

    @Query("SELECT * FROM manga where id = :id")
    fun getById(id: Long): Maybe<Manga>

    @Query("SELECT * FROM manga where mangaAlias = :alias")
    fun getByAlias(alias: String): Maybe<Manga>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg mangas: Manga)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(manga: Manga): Long

    @Delete
    fun delete(manga: Manga)

    @Update
    fun update(manga: Manga)
}