package com.arnaudpiroelle.manga.service

import android.app.job.JobParameters
import android.app.job.JobService
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.data.core.db.dao.ChapterDao
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.core.db.dao.TaskDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Chapter.Status.WANTED
import com.arnaudpiroelle.manga.data.model.Task
import com.arnaudpiroelle.manga.data.model.Task.Status.NEW
import com.arnaudpiroelle.manga.data.model.Task.Type.DOWNLOAD_CHAPTER
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.koin.android.ext.android.inject
import timber.log.Timber

class CheckerService : JobService(), CoroutineScope {

    private val mangaDao: MangaDao by inject()
    private val chapterDao: ChapterDao by inject()
    private val taskDao: TaskDao by inject()
    private val providerRegistry: ProviderRegistry by inject()

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    override fun onStartJob(params: JobParameters?): Boolean {

        Timber.d("Checker service started")
        launch {
            fetchNewChapters()
            createTasksForNewChapters()
        }

        return true
    }

    private suspend fun createTasksForNewChapters() {
        withContext(IO) {
            val wantedChapters = chapterDao.getByStatus(WANTED)
            wantedChapters.forEach { chapter ->
                val task = taskDao.get(DOWNLOAD_CHAPTER, chapter.id)
                if (task == null) {
                    taskDao.insert(Task(status = NEW, item = chapter.id))
                }
            }
        }
    }

    private suspend fun fetchNewChapters() {
        withContext(IO) {
            val mangas = mangaDao.getAll()
            mangas.forEach { manga ->
                val provider = providerRegistry.find(manga.provider)
                val chapters = provider.findChapters(manga.alias)

                chapters.forEachIndexed { index, chapter ->

                    val existingChapter = chapterDao.getByNumber(manga.id, chapter.chapterNumber)
                    if (existingChapter == null) {
                        val newChapter = Chapter(name = chapter.name, number = chapter.chapterNumber, mangaId = manga.id, status = WANTED)
                        val chapterId = chapterDao.insert(newChapter)
                    }
                }
            }
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        job.cancel()
        return true
    }

}