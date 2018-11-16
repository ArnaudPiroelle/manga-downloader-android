package com.arnaudpiroelle.manga.service.task

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.PostProcessType
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.arnaudpiroelle.manga.core.utils.FileHelper
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.data.core.db.dao.ChapterDao
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.core.db.dao.PageDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.data.model.Page
import com.arnaudpiroelle.manga.data.model.Page.PostProcess.MOSAIC
import com.arnaudpiroelle.manga.data.model.Page.PostProcess.NONE
import com.arnaudpiroelle.manga.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.Okio
import timber.log.Timber
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DownloadChapterTaskExecution(
        private val mangaDao: MangaDao,
        private val chapterDao: ChapterDao,
        private val pageDao: PageDao,
        private val providerRegistry: ProviderRegistry,
        private val fileHelper: FileHelper,
        private val preferencesHelper: PreferencesHelper) : TaskExecution {

    override suspend operator fun invoke(task: Task) {
        return withContext(Dispatchers.IO) {
            val chapterId = task.id
            val chapter = chapterDao.getById(chapterId)
            val manga = mangaDao.getById(chapter.mangaId)
            val pages = pageDao.getPagesFor(chapterId)

            val provider = providerRegistry.find(manga.provider)

            if (chapter.status == Chapter.Status.WANTED) {
                pages.forEachIndexed { index, page ->
                    val pageFile = fileHelper.getPageFile(manga, chapter, page, index)
                    val response = provider.findPage(page.url)

                    val source = response.body()!!.source()
                    val sink = Okio.buffer(Okio.sink(pageFile))
                    sink.writeAll(source)
                    sink.close()

                    postProcess(provider, page.postProcess, pageFile)
                }

                if (preferencesHelper.isCompressChapter()) {
                    compressChapter(manga, chapter)
                }

                chapterDao.insert(chapter.copy(status = Chapter.Status.DOWNLOADED))
                Unit
            } else {
                Timber.w("Chapter ${chapter.name} not wanted anymore. It will be skipped.")
            }
        }
    }

    private fun postProcess(provider: MangaProvider, pagePostProcess: Page.PostProcess, page: File) {
        val postProcess = when (pagePostProcess) {
            NONE -> PostProcessType.NONE
            MOSAIC -> PostProcessType.MOSAIC
            else -> PostProcessType.NONE
        }

        provider.postProcess(postProcess, page)
    }

    private fun compressChapter(manga: Manga, chapter: Chapter) {
        val chapterFile = fileHelper.getChapterCBZFile(manga, chapter)
        val chapterFolder = fileHelper.getChapterFolder(manga, chapter)

        if (chapterFile.exists()) {
            chapterFile.delete()
        }
        chapterFile.createNewFile()

        FileOutputStream(chapterFile).use { dest ->
            ZipOutputStream(BufferedOutputStream(dest)).use { out ->
                val buffer = 1024
                val data = ByteArray(buffer)

                for (file in chapterFolder.listFiles()) {
                    FileInputStream(file).use { fi ->
                        BufferedInputStream(fi, buffer).use { origin ->

                            val entry = ZipEntry(file.name)
                            out.putNextEntry(entry)

                            var count = origin.read(data, 0, buffer)
                            while (count != -1) {
                                out.write(data, 0, count)
                                count = origin.read(data, 0, buffer)
                            }
                        }
                    }
                }

                for (file in chapterFolder.listFiles()) {
                    file.delete()
                }

                chapterFolder.delete()
            }
        }

    }

}