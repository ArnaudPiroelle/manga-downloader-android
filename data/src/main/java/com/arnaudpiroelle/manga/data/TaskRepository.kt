package com.arnaudpiroelle.manga.data

import com.arnaudpiroelle.manga.data.core.db.dao.TaskDao
import com.arnaudpiroelle.manga.data.model.Task
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getAll(): Flowable<List<Task>> {
        return taskDao.getAll()
    }

    fun findByStatus(status: Task.Status): Flowable<List<Task>> {
        return taskDao.findByStatus(status)
    }

    fun insert(task: Task): Maybe<Long> {
        return Maybe.fromCallable { taskDao.insert(task) }
    }

    fun delete(task: Task): Completable {
        return Completable.fromCallable { taskDao.delete(task) }
    }

    fun update(task: Task): Completable {
        return Completable.fromCallable { taskDao.update(task) }
    }

}