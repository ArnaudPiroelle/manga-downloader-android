package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.arnaudpiroelle.manga.data.model.Manga

@Dao
interface MangaDao {

    @Query("SELECT * FROM mangas")
    fun getAll(): List<Manga>

    @Query("SELECT * FROM mangas where provider = :provider")
    fun getMangaForProvider(provider: String): List<Manga>

    @Query("SELECT * FROM mangas where id = :id")
    fun getById(id: Long): Manga

    @Query("SELECT * FROM mangas where alias = :alias")
    fun getByAlias(alias: String): Manga

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(manga: Manga): Long

    @Delete
    fun delete(manga: Manga)

    @Update
    fun update(manga: Manga)

    @Query("SELECT * FROM mangas ORDER BY name")
    fun observeAll(): DataSource.Factory<Int, Manga>
}