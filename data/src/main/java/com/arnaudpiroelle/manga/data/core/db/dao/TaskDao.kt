package com.arnaudpiroelle.manga.data.core.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.arnaudpiroelle.manga.data.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks where status in(:status)")
    fun findByStatus(vararg status: Task.Status): List<Task>

    @Insert
    fun insert(task: Task): Long

    @Delete
    fun delete(task: Task)

    @Update
    fun update(task: Task)

    @Query("SELECT * FROM tasks")
    fun observeAll(): DataSource.Factory<Int, Task>

    @Query("SELECT * FROM tasks where type = :type and item = :item")
    fun get(type: Task.Type, item: Long): Task?
}