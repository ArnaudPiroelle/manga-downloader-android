package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import com.arnaudpiroelle.manga.data.model.Page

@Dao
interface PageDao {
    @Insert
    fun insert(page: Page): Long
}