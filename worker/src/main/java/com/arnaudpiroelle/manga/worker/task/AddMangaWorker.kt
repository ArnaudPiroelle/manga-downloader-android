package com.arnaudpiroelle.manga.worker.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.data.dao.MangaDao
import com.arnaudpiroelle.manga.data.model.Manga
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

class AddMangaWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams), KoinComponent {

    private val mangaDao: MangaDao by inject()
    private val providerRegistry: ProviderRegistry by inject()

    override fun doWork(): Result {
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
            val chapters = provider.findChapters(manga.alias)
            val firstChapter = chapters.firstOrNull()

            if (firstChapter != null) {
                val pages = provider.findPages(manga.alias, firstChapter.chapterNumber)
                val firstPage = pages.firstOrNull()

                if (firstPage != null) {
                    mangaDao.update(manga.copy(thumbnail = firstPage.url, status = Manga.Status.INITIALIZED))
                }
            }

            Timber.d("AddMangaWorker ended with success")

            return Result.success()
        } catch (e: Exception) {
            Timber.d("AddMangaWorker ended with error")

            return Result.retry()
        }
    }

    companion object {
        const val INPUT_MANGA_ID = "MANGA_ID"
    }

}