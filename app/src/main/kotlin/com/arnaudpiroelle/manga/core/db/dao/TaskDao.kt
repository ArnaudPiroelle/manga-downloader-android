package com.arnaudpiroelle.manga.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.arnaudpiroelle.manga.model.db.Task
import io.reactivex.Flowable

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAll(): Flowable<List<Task>>

    @Query("SELECT * FROM tasks where status = :status")
    fun findByStatus(status: Task.Status): Flowable<Task>

    @Insert
    fun insert(task: Task): Long
}