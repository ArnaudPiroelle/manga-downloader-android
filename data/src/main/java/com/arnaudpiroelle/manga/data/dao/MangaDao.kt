package com.arnaudpiroelle.manga.data.dao

import androidx.paging.DataSource
import androidx.room.*
import com.arnaudpiroelle.manga.data.model.Manga

@Dao
interface MangaDao {

    @Query("SELECT * FROM mangas")
    suspend fun getAll(): List<Manga>

    @Query("SELECT * FROM mangas WHERE status = :status")
    suspend fun getAll(status: Manga.Status): List<Manga>


    @Query("SELECT * FROM mangas where provider = :provider")
    suspend fun getMangaForProvider(provider: String): List<Manga>

    @Query("SELECT * FROM mangas where id = :id")
    suspend fun getById(id: Long): Manga?

    @Query("SELECT * FROM mangas where alias = :alias")
    suspend fun getByAlias(alias: String): Manga?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(manga: Manga): Long

    @Delete
    suspend fun delete(manga: Manga)

    @Query("DELETE FROM mangas WHERE id = :mangaId")
    suspend fun deleteById(mangaId: Long)

    @Update
    suspend fun update(manga: Manga)

    @Query("SELECT * FROM mangas ORDER BY name")
    fun observeAll(): DataSource.Factory<Int, Manga>
}