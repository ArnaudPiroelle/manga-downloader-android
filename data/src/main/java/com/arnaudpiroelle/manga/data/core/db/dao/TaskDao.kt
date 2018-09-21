package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.room.*
import com.arnaudpiroelle.manga.data.model.Task
import io.reactivex.Flowable

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAll(): Flowable<List<Task>>

    @Query("SELECT * FROM tasks where status = :status")
    fun findByStatus(status: Task.Status): Flowable<List<Task>>

    @Insert
    fun insert(task: Task): Long

    @Delete
    fun delete(task: Task)

    @Update
    fun update(task: Task)
}