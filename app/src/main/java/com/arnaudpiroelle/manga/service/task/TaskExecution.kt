package com.arnaudpiroelle.manga.service.task

import com.arnaudpiroelle.manga.data.model.Task

interface TaskExecution {
    suspend fun invoke(task: Task)
}