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
    suspend fun getAll(): List<History>

    @Query("DELETE FROM history")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(vararg histories: History)

    @Query("SELECT * FROM history ORDER BY date DESC")
    fun observeAll(): DataSource.Factory<Int, History>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History): Long
}