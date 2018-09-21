package com.arnaudpiroelle.manga.service

import android.app.job.JobParameters
import android.app.job.JobService
import com.arnaudpiroelle.manga.api.core.rx.plusAssign
import com.arnaudpiroelle.manga.core.db.dao.TaskDao
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.model.db.Task
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TaskService : JobService() {

    @Inject
    lateinit var taskDao: TaskDao

    val subscriptions = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()

        inject()
    }

    override fun onStartJob(params: JobParameters?): Boolean {

        println("TaskService started")

        subscriptions += taskDao.findByStatus(Task.Status.NEW)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    println(it)
                }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        println("TaskService stopped")

        return true
    }
}