package com.arnaudpiroelle.manga.service.task

import com.arnaudpiroelle.manga.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadChapterTaskExecution : TaskExecution {
    override suspend operator fun invoke(task: Task) {
        return withContext(Dispatchers.IO) { }
    }
}