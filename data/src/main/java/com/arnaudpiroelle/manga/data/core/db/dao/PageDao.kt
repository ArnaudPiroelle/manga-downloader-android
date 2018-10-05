package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.arnaudpiroelle.manga.data.model.Page
import io.reactivex.Maybe

@Dao
interface PageDao {
    @Insert
    fun insert(page: Page): Long

    @Query("SELECT * from pages WHERE chapterId = :chapterId")
    fun getPagesFor(chapterId: Long): Maybe<List<Page>>
}