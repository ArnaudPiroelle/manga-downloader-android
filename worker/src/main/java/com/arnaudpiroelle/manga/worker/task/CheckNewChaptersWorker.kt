package com.arnaudpiroelle.manga.worker.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.data.dao.ChapterDao
import com.arnaudpiroelle.manga.data.dao.MangaDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.worker.TaskManager
import com.arnaudpiroelle.manga.worker.notification.NotificationCenter
import com.arnaudpiroelle.manga.worker.notification.NotificationCenter.Notification.*
import com.arnaudpiroelle.manga.worker.utils.FileHelper
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

class CheckNewChaptersWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams), KoinComponent {

    private val notificationCenter: NotificationCenter by inject()
    private val mangaDao: MangaDao by inject()
    private val chapterDao: ChapterDao by inject()
    private val providerRegistry: ProviderRegistry by inject()
    private val taskManager: TaskManager by inject()
    private val fileHelper: FileHelper by inject()

    override fun doWork(): Result {
        Timber.d("CheckNewChaptersWorker started")

        notificationCenter.notify(SyncStarted)
        try {
            val mangas = mangaDao.getAll()
            val totalMangas = mangas.size
            mangas.forEachIndexed { index, manga ->
                notificationCenter.notify(SyncProgress(index, totalMangas, manga.name))

                checkManga(manga)
            }

            val wantedChapters = chapterDao.getByStatus(Chapter.Status.WANTED)
            val errorChapters = chapterDao.getByStatus(Chapter.Status.ERROR)
            val chapters = listOf(wantedChapters, errorChapters).flatten()

            println(chapters)
            taskManager.scheduleDownloadChapter(chapters.map { it.id })

            Timber.d("CheckNewChaptersWorker ended with success")
        } catch (e: Exception) {
            Timber.e(e, "CheckNewChaptersWorker ended with error")
        }

        notificationCenter.notify(SyncEnded)
        return Result.success()
    }

    private fun checkManga(manga: Manga) {
        val provider = providerRegistry.find(manga.provider)
        if (provider == null) {
            Timber.w("Provider not existing. Manga will be ignored")
            return
        }

        val chapters = provider.findChapters(manga.alias)
        when (manga.status) {
            Manga.Status.INITIALIZED -> fetchInitializedManga(manga, chapters)
            Manga.Status.ENABLED -> fetchNewChapters(manga, chapters)
            else -> Timber.d("Manga has ADDED or DISABLED status. It will be ignored.")
        }
    }

    private fun fetchInitializedManga(manga: Manga, chapters: List<com.arnaudpiroelle.manga.api.model.Chapter>) {
        chapters.forEachIndexed { index, chapter ->
            val existingChapter = chapterDao.getByNumber(manga.id, chapter.chapterNumber)
            if (existingChapter == null) {
                val newChapter = Chapter(name = chapter.name, number = chapter.chapterNumber, mangaId = manga.id, status = Chapter.Status.SKIPPED)

                val cbzFile = fileHelper.getChapterCBZFile(manga, newChapter)
                val newStatus = if (cbzFile.exists()) {
                    Chapter.Status.DOWNLOADED
                } else {
                    Chapter.Status.SKIPPED
                }
                chapterDao.insert(newChapter.copy(status = newStatus))
            }

        }

        mangaDao.update(manga.copy(status = Manga.Status.ENABLED))
    }

    private fun fetchNewChapters(manga: Manga, chapters: List<com.arnaudpiroelle.manga.api.model.Chapter>) {
        chapters.forEachIndexed { _, chapter ->
            val existingChapter = chapterDao.getByNumber(manga.id, chapter.chapterNumber)
            if (existingChapter == null) {
                val newChapter = Chapter(name = chapter.name, number = chapter.chapterNumber, mangaId = manga.id, status = Chapter.Status.WANTED)

                val cbzFile = fileHelper.getChapterCBZFile(manga, newChapter)
                val newStatus = if (cbzFile.exists()) {
                    Chapter.Status.DOWNLOADED
                } else {
                    Chapter.Status.WANTED
                }

                chapterDao.insert(newChapter.copy(status = newStatus))
            }
        }
    }
}