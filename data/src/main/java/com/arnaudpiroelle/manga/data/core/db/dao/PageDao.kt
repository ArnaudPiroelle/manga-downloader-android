package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnaudpiroelle.manga.data.model.Page

@Dao
interface PageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(page: Page): Long

    @Query("SELECT * from pages WHERE chapterId = :chapterId")
    fun getPagesFor(chapterId: Long): List<Page>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(pages: List<Page>)

    @Query("DELETE FROM pages WHERE chapterId = :chapterId")
    fun removeAllFor(chapterId: Long)
}