package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.arnaudpiroelle.manga.data.model.Chapter
import io.reactivex.Maybe

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapters where mangaId = :mangaId ORDER BY id ASC LIMIT 1")
    fun getFirstChapterFor(mangaId: Long): Maybe<Chapter>

    @Query("SELECT * FROM chapters where mangaId = :mangaId ORDER BY id DESC LIMIT 1")
    fun getLastChapterFor(mangaId: Long): Maybe<Chapter>

    @Insert
    fun insertAll(chapters: List<Chapter>)

    @Insert
    fun insert(chapter: Chapter): Long

}