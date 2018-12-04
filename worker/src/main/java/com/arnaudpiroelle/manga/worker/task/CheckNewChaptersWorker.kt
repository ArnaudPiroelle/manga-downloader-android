package com.arnaudpiroelle.manga.worker.task

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.PostProcessType
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.arnaudpiroelle.manga.data.core.db.dao.ChapterDao
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.core.db.dao.PageDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Chapter.Status.CREATED
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.data.model.Page
import com.arnaudpiroelle.manga.worker.TaskManager
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import timber.log.Timber

class CheckNewChaptersWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams), KoinComponent {

    private val mangaDao: MangaDao by inject()
    private val chapterDao: ChapterDao by inject()
    private val pageDao: PageDao by inject()
    private val providerRegistry: ProviderRegistry by inject()
    private val taskManager: TaskManager by inject()

    override fun doWork(): Result {
        Timber.d("CheckNewChaptersWorker started")

        try {
            val mangas = mangaDao.getAll(true)
            mangas.forEach(this::checkManga)

            val chapters = chapterDao.getByStatus(Chapter.Status.WANTED)
            taskManager.scheduleDownloadChapter(chapters.map { it.id })

            Timber.d("CheckNewChaptersWorker ended with success")
        } catch (e: Exception) {
            Timber.e(e, "CheckNewChaptersWorker ended with error")
        }
        return Result.SUCCESS
    }

    private fun checkManga(manga: Manga) {
        val provider = providerRegistry.find(manga.provider)
        if (provider != null) {
            val chapters = provider.findChapters(manga.alias)

            chapters.forEachIndexed { index, chapter ->
                checkChapter(provider, manga, chapter, index)
            }
        }
    }

    private fun checkChapter(provider: MangaProvider, manga: Manga, chapter: com.arnaudpiroelle.manga.api.model.Chapter, index: Int) {
        val existingChapter = chapterDao.getByNumber(manga.id, chapter.chapterNumber)

        if (existingChapter != null && existingChapter.status == CREATED) {
            val newChapter = Chapter(name = chapter.name, number = chapter.chapterNumber, mangaId = manga.id, status = CREATED)
            val chapterId = chapterDao.insert(newChapter)

            val pages = provider.findPages(manga.alias, newChapter.number)
                    .map {
                        val postProcess = when (it.postProcess) {
                            PostProcessType.NONE -> Page.PostProcess.NONE
                            PostProcessType.MOSAIC -> Page.PostProcess.MOSAIC
                        }
                        Page(chapterId = chapterId, url = it.url, postProcess = postProcess)
                    }

            pageDao.insertAll(pages)
            chapterDao.update(newChapter.copy(status = Chapter.Status.WANTED))

            if (index == 0) {
                val thumbnail = pages.first().url
                mangaDao.update(manga.copy(thumbnail = thumbnail, enable = true))
            }
        }
    }
}