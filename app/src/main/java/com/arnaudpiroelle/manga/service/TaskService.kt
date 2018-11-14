package com.arnaudpiroelle.manga.service

import android.app.job.JobParameters
import android.app.job.JobService
import com.arnaudpiroelle.manga.data.core.db.dao.TaskDao
import com.arnaudpiroelle.manga.data.model.Task
import com.arnaudpiroelle.manga.service.task.DownloadChapterTaskExecution
import com.arnaudpiroelle.manga.service.task.RetrieveChaptersTaskExecution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import timber.log.Timber

class TaskService : JobService(), CoroutineScope {

    private val taskDao: TaskDao by inject()
    private val retrieveChaptersTaskExecution: RetrieveChaptersTaskExecution by inject()
    private val downloadChapterTaskExecution: DownloadChapterTaskExecution by inject()

    private val job = Job()

    override val coroutineContext = job + Main

    override fun onStartJob(params: JobParameters?): Boolean {
        Timber.d("TaskService started")

        launch {
            withContext(IO) {
                val tasks = taskDao.findByStatus(Task.Status.NEW, Task.Status.IN_PROGRESS)
                tasks.forEach { task ->
                    taskDao.update(task.copy(status = Task.Status.IN_PROGRESS))
                    executeTask(task)
                    taskDao.update(task.copy(status = Task.Status.SUCCESS))
                }

                Timber.d("End of process")
                jobFinished(params, true)
            }
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Timber.d("TaskService stopped")

        job.cancel()

        return true
    }

    private suspend fun executeTask(task: Task) {
        return when (task.type) {
            Task.Type.RETRIEVE_CHAPTERS -> retrieveChaptersTaskExecution(task)
            Task.Type.DOWNLOAD_CHAPTER -> downloadChapterTaskExecution(task)
        }
    }

}