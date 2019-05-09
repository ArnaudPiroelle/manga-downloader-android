package com.arnaudpiroelle.manga.data.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arnaudpiroelle.manga.data.model.History

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Query("DELETE FROM history")
    fun deleteAll()

    @Insert
    fun insertAll(vararg histories: History)

    @Query("SELECT * FROM history")
    fun observeAll(): DataSource.Factory<Int, History>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: History): Long
}