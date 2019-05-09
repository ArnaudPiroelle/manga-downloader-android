package com.arnaudpiroelle.manga.data.dao

import androidx.paging.DataSource
import androidx.room.*
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Chapter.Status

@Dao
interface ChapterDao {

    @Insert
    suspend fun insertAll(chapters: List<Chapter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chapter: Chapter): Long

    @Update
    suspend fun update(chapter: Chapter)

    @Query("UPDATE chapters SET status = :status where mangaId = :mangaId")
    suspend fun setAllChaptersStatusAs(mangaId: Long, status: Status)

    @Query("SELECT * from chapters where id = :id")
    suspend fun getById(id: Long): Chapter?

    @Query("SELECT * from chapters where status = :status")
    suspend fun getByStatus(status: Status): List<Chapter>

    @Query("SELECT * FROM chapters where mangaId = :mangaId and number = :number")
    suspend fun getByNumber(mangaId: Long, number: String): Chapter?

    @Query("SELECT * FROM chapters where mangaId = :mangaId and number = :number and status = :status")
    suspend fun getByNumberAndStatus(mangaId: Long, number: String, status: Status): Chapter?

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId ORDER BY id DESC")
    fun observeAll(mangaId: Long): DataSource.Factory<Int, Chapter>

}