package com.arnaudpiroelle.manga.service.task

import com.arnaudpiroelle.manga.data.model.Task
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadChapterTaskExecution @Inject constructor() : TaskExecution {

    override fun execute(task: Task): Completable {
        return Completable.complete()
    }

}