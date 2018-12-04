package com.arnaudpiroelle.manga.worker.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.PostProcessType
import com.arnaudpiroelle.manga.data.core.db.dao.ChapterDao
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.core.db.dao.PageDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Chapter.Status.CREATED
import com.arnaudpiroelle.manga.data.model.Chapter.Status.WANTED
import com.arnaudpiroelle.manga.data.model.Page
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

class AddMangaWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams), KoinComponent {

    private val mangaDao: MangaDao by inject()
    private val chapterDao: ChapterDao by inject()
    private val pageDao: PageDao by inject()
    private val providerRegistry: ProviderRegistry by inject()

    override fun doWork(): Result {
        Timber.d("AddMangaWorker started")
        val mangaId = inputData.getLong(INPUT_MANGA_ID, -1L)
        if (mangaId == -1L) {
            Timber.d("Field mangaId is mandatory. Worker ended with failure")
            return Result.SUCCESS
        }
        val manga = mangaDao.getById(mangaId)
        if (manga == null) {
            Timber.e("Manga not existing. DownloadChapterWorker ended with failure")
            return Result.SUCCESS
        }
        val provider = providerRegistry.find(manga.provider)
        if (provider == null) {
            Timber.e("Provider not existing. DownloadChapterWorker ended with failure")
            return Result.SUCCESS
        }


        try {
            val chapters = provider.findChapters(manga.alias)

            chapters.forEachIndexed { index, chapter ->
                val existingChapter = chapterDao.getByNumber(mangaId, chapter.chapterNumber)

                val chapterToProcess = if (existingChapter == null) {
                    val newChapter = Chapter(name = chapter.name, number = chapter.chapterNumber, mangaId = manga.id, status = CREATED)
                    val chapterId = chapterDao.insert(newChapter)
                    newChapter.copy(id = chapterId)
                } else {
                    pageDao.removeAllFor(chapterId = existingChapter.id)
                    existingChapter
                }

                val pages = provider.findPages(manga.alias, chapterToProcess.number)
                        .map {
                            val postProcess = when (it.postProcess) {
                                PostProcessType.NONE -> Page.PostProcess.NONE
                                PostProcessType.MOSAIC -> Page.PostProcess.MOSAIC
                            }
                            Page(chapterId = chapterToProcess.id, url = it.url, postProcess = postProcess)
                        }

                pageDao.insertAll(pages)
                chapterDao.update(chapterToProcess.copy(status = WANTED))

                if (index == 0) {
                    val thumbnail = pages.first().url
                    mangaDao.update(manga.copy(thumbnail = thumbnail, enable = true))
                }
            }

            Timber.d("AddMangaWorker ended with success")
        } catch (e: Exception) {
            Timber.d("AddMangaWorker ended with error")

            //TODO: return RETRY
        }

        return Result.SUCCESS
    }

    companion object {
        const val INPUT_MANGA_ID = "MANGA_ID"
    }

}