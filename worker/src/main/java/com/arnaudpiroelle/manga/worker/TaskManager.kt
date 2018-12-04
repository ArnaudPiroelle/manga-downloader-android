package com.arnaudpiroelle.manga.worker

import androidx.work.*
import com.arnaudpiroelle.manga.worker.task.AddMangaWorker
import com.arnaudpiroelle.manga.worker.task.AddMangaWorker.Companion.INPUT_MANGA_ID
import com.arnaudpiroelle.manga.worker.task.CheckNewChaptersWorker
import com.arnaudpiroelle.manga.worker.task.DownloadChapterWorker
import com.arnaudpiroelle.manga.worker.task.DownloadChapterWorker.Companion.INPUT_CHAPTER_ID
import java.util.concurrent.TimeUnit.MINUTES

class TaskManager(private val workerManager: WorkManager) {

    fun scheduleAddManga(mangaId: Long) {
        val request = createAddMangaWork(mangaId)

        workerManager.enqueueUniqueWork(TAG_ADD_MANGA, ExistingWorkPolicy.APPEND, request)
    }

    fun scheduleManualCheckNewChapters() {
        val request = OneTimeWorkRequestBuilder<CheckNewChaptersWorker>()
                //.setBackoffCriteria(BackoffPolicy.LINEAR, 5, MINUTES)
                .setConstraints(Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build()
        workerManager.enqueueUniqueWork(TAG_PERIODIC_CHECK_NEW_CHAPTERS, ExistingWorkPolicy.APPEND, request)
    }

    fun schedulePeriodicCheckNewChapters() {
        val request = PeriodicWorkRequestBuilder<CheckNewChaptersWorker>(15, MINUTES)
                //.setBackoffCriteria(BackoffPolicy.LINEAR, 5, MINUTES)
                .setConstraints(Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .build()
        workerManager.enqueueUniquePeriodicWork(TAG_PERIODIC_CHECK_NEW_CHAPTERS, ExistingPeriodicWorkPolicy.REPLACE, request)
    }

    fun scheduleDownloadChapter(ids: List<Long>) {
        val works = ids.map(this::createDownloadChapterWork)

        workerManager.enqueueSequentialWork(TAG_DOWNLOAD_CHAPTER, ExistingWorkPolicy.APPEND, works)
    }

    private fun createDownloadChapterWork(chapterId: Long) = OneTimeWorkRequestBuilder<DownloadChapterWorker>()
            //.setBackoffCriteria(BackoffPolicy.LINEAR, 5, MINUTES)
            .setInputData(Data.Builder()
                    .putLong(INPUT_CHAPTER_ID, chapterId)
                    .build())
            .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build())
            .build()

    private fun createAddMangaWork(mangaId: Long) = OneTimeWorkRequestBuilder<AddMangaWorker>()
            //.setBackoffCriteria(BackoffPolicy.LINEAR, 5, MINUTES)
            .setInputData(Data.Builder()
                    .putLong(INPUT_MANGA_ID, mangaId)
                    .build())
            .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build())
            .build()


    companion object {
        const val TAG_ADD_MANGA = "TAG_ADD_MANGA"
        const val TAG_DOWNLOAD_CHAPTER = "TAG_DOWNLOAD_CHAPTER"
        const val TAG_PERIODIC_CHECK_NEW_CHAPTERS = "TAG_PERIODIC_CHECK_NEW_CHAPTERS"
    }

    private fun WorkManager.enqueueSequentialWork(tag: String, policy: ExistingWorkPolicy, works: List<OneTimeWorkRequest>): Operation? {
        if (works.isEmpty()) {
            return null
        }

        var workContinuation = this.beginUniqueWork(tag, policy, works[0])

        works.subList(1, works.size).forEach {
            workContinuation = workContinuation.then(it)
        }

        return workContinuation.enqueue()
    }
}