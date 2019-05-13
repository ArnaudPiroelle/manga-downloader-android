package com.arnaudpiroelle.manga.worker.task

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.data.dao.ChapterDao
import com.arnaudpiroelle.manga.data.dao.MangaDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.worker.utils.FileHelper
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class AddMangaWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams), KoinComponent {

    private val mangaDao: MangaDao by inject()
    private val chapterDao: ChapterDao by inject()
    private val providerRegistry: ProviderRegistry by inject()
    private val fileHelper: FileHelper by inject()

    override suspend fun doWork(): Result {
        Timber.d("AddMangaWorker started")
        val mangaId = inputData.getLong(INPUT_MANGA_ID, -1L)
        if (mangaId == -1L) {
            Timber.d("Field mangaId is mandatory. Worker ended with failure")
            return Result.success()
        }
        val manga = mangaDao.getById(mangaId)
        if (manga == null) {
            Timber.e("Manga not existing. DownloadChapterWorker ended with failure")
            return Result.success()
        }
        val provider = providerRegistry.find(manga.provider)
        if (provider == null) {
            Timber.e("Provider not existing. DownloadChapterWorker ended with failure")
            return Result.success()
        }


        try {
            val details = provider.findDetails(manga.alias)
            val chapters = provider.findChapters(manga.alias)
            val firstChapter = chapters.firstOrNull()

            val thumbnail = if (firstChapter != null) {
                val pages = provider.findPages(manga.alias, firstChapter.chapterNumber)
                pages.firstOrNull()?.url ?: ""
            } else {
                ""
            }

            val copy = manga.copy(
                    thumbnail = thumbnail,
                    status = Manga.Status.INITIALIZED,
                    author = details.author,
                    kind = details.kind,
                    origin = details.origin,
                    summary = details.summary,
                    type = details.type,
                    year = details.year
            )
            mangaDao.update(copy)

            fetchInitializedManga(copy, chapters)

            Timber.d("AddMangaWorker ended with success")

            return Result.success()
        } catch (e: Exception) {
            Timber.d("AddMangaWorker ended with error")

            return Result.retry()
        }
    }

    private suspend fun fetchInitializedManga(manga: Manga, chapters: List<com.arnaudpiroelle.manga.api.model.Chapter>) {
        chapters.forEachIndexed { _, chapter ->
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
            } else {
                chapterDao.update(existingChapter.copy(name = chapter.name))
            }

        }

        mangaDao.update(manga.copy(status = Manga.Status.ENABLED))
    }

    companion object {
        const val INPUT_MANGA_ID = "MANGA_ID"
    }

}