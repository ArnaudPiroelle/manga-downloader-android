package com.arnaudpiroelle.manga.service.task

import com.arnaudpiroelle.manga.data.model.Task
import io.reactivex.Completable

interface TaskExecution {
    fun execute(task: Task): Completable
}