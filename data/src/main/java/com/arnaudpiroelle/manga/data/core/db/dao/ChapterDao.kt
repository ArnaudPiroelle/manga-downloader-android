package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.room.*
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Chapter.Status

@Dao
interface ChapterDao {

    @Insert
    fun insertAll(chapters: List<Chapter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chapter: Chapter): Long

    @Update
    fun update(chapter: Chapter)

    @Query("SELECT * from chapters where id = :id")
    fun getById(id: Long): Chapter?

    @Query("SELECT * from chapters where status = :status")
    fun getByStatus(status: Status): List<Chapter>

    @Query("SELECT * FROM chapters where mangaId = :mangaId and number = :number")
    fun getByNumber(mangaId: Long, number: String): Chapter?

    @Query("SELECT * FROM chapters where mangaId = :mangaId and number = :number and status = :status")
    fun getByNumberAndStatus(mangaId: Long, number: String, status: Status): Chapter?

}