package com.arnaudpiroelle.manga.service

import android.app.job.JobParameters
import android.app.job.JobService
import com.arnaudpiroelle.manga.api.core.rx.plusAssign
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.data.TaskRepository
import com.arnaudpiroelle.manga.data.model.Task
import com.arnaudpiroelle.manga.service.task.DownloadChapterTaskExecution
import com.arnaudpiroelle.manga.service.task.RetrieveChaptersTaskExecution
import io.reactivex.Completable
import io.reactivex.Flowable.fromIterable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class TaskService : JobService() {

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var retrieveChaptersTaskExecution: RetrieveChaptersTaskExecution

    @Inject
    lateinit var downloadChapterTaskExecution: DownloadChapterTaskExecution

    private val subscriptions = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()

        inject()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Timber.d("TaskService started")

        subscriptions += taskRepository.findByStatus(Task.Status.NEW)
                .subscribeOn(Schedulers.io())
                .concatMapCompletable(this::process)
                .subscribe {
                    println("End of process")
                }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Timber.d("TaskService stopped")

        subscriptions.clear()

        return true
    }

    private fun process(tasks: List<Task>): Completable {
        return fromIterable(tasks)
                .distinct()
                .concatMapCompletable(this::processTask)
    }

    private fun processTask(task: Task): Completable {
        return taskRepository.update(task.copy(status = Task.Status.IN_PROGRESS))
                .andThen(executeTask(task))
                .andThen(taskRepository.update(task.copy(status = Task.Status.SUCCESS)))
    }

    private fun executeTask(task: Task): Completable {
        return when (task.type) {
            Task.Type.RETRIEVE_CHAPTERS -> retrieveChaptersTaskExecution.execute(task)
            Task.Type.DOWNLOAD_CHAPTER -> downloadChapterTaskExecution.execute(task)
        }
    }

}