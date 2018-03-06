package com.arnaudpiroelle.manga.core.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.arnaudpiroelle.manga.model.db.History
import io.reactivex.Flowable

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll(): Flowable<List<History>>

    @Query("DELETE FROM history")
    fun deleteAll()

    @Insert
    fun insertAll(vararg histories: History)
}