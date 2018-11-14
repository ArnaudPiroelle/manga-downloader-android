package com.arnaudpiroelle.manga.service.task

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.PostProcessType
import com.arnaudpiroelle.manga.data.core.db.dao.ChapterDao
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.core.db.dao.PageDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Page
import com.arnaudpiroelle.manga.data.model.Task
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class RetrieveChaptersTaskExecution(
        private val mangaDao: MangaDao,
        private val chapterDao: ChapterDao,
        private val pageDao: PageDao,
        private val providerRegistry: ProviderRegistry
) : TaskExecution {

    override suspend operator fun invoke(task: Task) {
        return withContext(IO) {
            val manga = mangaDao.getById(task.id)
            val provider = providerRegistry.find(manga.provider)

            val chapters = provider.findChapters(manga.alias)
            chapters.forEachIndexed { index, chapter ->

                val newChapter = Chapter(name = chapter.name, number = chapter.chapterNumber, mangaId = manga.id, status = Chapter.Status.WANTED)
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
                if (index == 0) {
                    val thumbnail = pages.first().url
                    mangaDao.update(manga.copy(thumbnail = thumbnail))
                }
            }


            Unit
        }
    }

}