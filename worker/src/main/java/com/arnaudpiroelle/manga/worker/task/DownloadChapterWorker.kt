package com.arnaudpiroelle.manga.worker.task

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.PostProcessType
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.arnaudpiroelle.manga.data.dao.ChapterDao
import com.arnaudpiroelle.manga.data.dao.MangaDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.worker.notification.NotificationCenter
import com.arnaudpiroelle.manga.worker.notification.NotificationCenter.Notification.*
import com.arnaudpiroelle.manga.worker.utils.FileHelper
import com.arnaudpiroelle.manga.worker.utils.PreferencesHelper
import okio.Okio
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DownloadChapterWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams), KoinComponent {

    private val notificationCenter: NotificationCenter by inject()
    private val mangaDao: MangaDao by inject()
    private val chapterDao: ChapterDao by inject()
    private val providerRegistry: ProviderRegistry by inject()
    private val fileHelper: FileHelper by inject()
    private val preferencesHelper: PreferencesHelper by inject()

    override suspend fun doWork(): Result {
        Timber.d("DownloadChapterWorker started")

        val chapterId = inputData.getLong(INPUT_CHAPTER_ID, -1L)
        if (chapterId == -1L) {
            Timber.e("Field chapterId is mandatory. DownloadChapterWorker ended with failure")
            return Result.success()
        }
        val chapter = chapterDao.getById(chapterId)
        if (chapter == null) {
            Timber.e("Chapter not existing. DownloadChapterWorker ended with failure")
            return Result.success()
        }
        val manga = mangaDao.getById(chapter.mangaId)
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
            if (chapter.status == Chapter.Status.WANTED || chapter.status == Chapter.Status.ERROR) {
                notificationCenter.notify(DownloadStarted(chapter.name))

                val pages = provider.findPages(manga.alias, chapter.number)
                if (pages.isEmpty()) {
                    return Result.success()
                }

                chapterDao.update(chapter.copy(status = Chapter.Status.DOWNLOADING))

                pages.forEachIndexed { index, page ->
                    notificationCenter.notify(DownloadProgress(index, pages.size, chapter.name))

                    val pageFile = fileHelper.getPageFile(manga, chapter, page, index)
                    val response = provider.findPage(page.url)

                    val source = response.source()
                    val sink = Okio.buffer(Okio.sink(pageFile))
                    sink.writeAll(source)
                    sink.close()

                    postProcess(provider, page.postProcess, pageFile)
                }

                if (preferencesHelper.isCompressChapter()) {
                    compressChapter(manga, chapter)
                }

                chapterDao.update(chapter.copy(status = Chapter.Status.DOWNLOADED))

                notificationCenter.notify(DownloadEnded(chapter.id, manga.name, chapter.number, Chapter.Status.DOWNLOADED))

                Timber.d("DownloadChapterWorker ended with success")
            } else {
                Timber.w("Chapter ${chapter.name} not wanted anymore. It will be skipped.")
            }
        } catch (e: Exception) {
            Timber.e(e, "DownloadChapterWorker ended with error")

            val chapterFolder = fileHelper.getChapterFolder(manga, chapter)
            val chapterFile = fileHelper.getChapterCBZFile(manga, chapter)

            chapterFolder.deleteRecursively()
            chapterFile.deleteRecursively()

            chapterDao.update(chapter.copy(status = Chapter.Status.ERROR))

            notificationCenter.notify(DownloadEnded(chapter.id, manga.name, chapter.number, Chapter.Status.ERROR))
        }

        return Result.success()
    }

    private suspend fun postProcess(provider: MangaProvider, postProcess: PostProcessType, page: File) {
        provider.postProcess(postProcess, page)
    }

    private fun compressChapter(manga: Manga, chapter: Chapter) {
        notificationCenter.notify(DownloadCompressStarted(chapter.name))

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

                val listFiles = chapterFolder.listFiles()
                val filesSize = listFiles.size
                listFiles.forEachIndexed { index, file ->

                    notificationCenter.notify(DownloadCompressProgress(index, filesSize, chapter.name))

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

                chapterFolder.deleteRecursively()
            }
        }

    }

    companion object {
        const val INPUT_CHAPTER_ID = "CHAPTER_ID"
    }
}