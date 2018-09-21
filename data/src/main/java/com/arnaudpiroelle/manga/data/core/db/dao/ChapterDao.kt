package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.arnaudpiroelle.manga.data.model.Chapter

@Dao
interface ChapterDao {
    @Insert
    fun insertAll(chapters: List<Chapter>)

    @Insert
    fun insert(chapter: Chapter): Long

}