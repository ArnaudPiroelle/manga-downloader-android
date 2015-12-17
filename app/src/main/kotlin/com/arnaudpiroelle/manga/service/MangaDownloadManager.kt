package com.arnaudpiroelle.manga.service

import android.support.annotation.VisibleForTesting
import android.util.Log
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.utils.FileHelper
import com.arnaudpiroelle.manga.core.utils.HttpUtils
import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page
import rx.Observable
import rx.Observable.from
import rx.schedulers.Schedulers
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MangaDownloadManager(private val callback: MangaDownloadManager.MangaDownloaderCallback, private val providerRegistry: ProviderRegistry, private val fileHelper: FileHelper) {

    fun startDownload(provider: MangaProvider, mangas: List<Manga>) {
        from(mangas)
                .subscribeOn(Schedulers.io())
                .forEach(
                        {
                            onMangaDownload(it)
                            downloadManga(provider, it)
                        },
                        { onError(it) },
                        { onAllMangasDownloaded() }
                )
    }

    private fun downloadManga(provider: MangaProvider, manga: Manga) {
        getChapters(provider, manga).forEach (
                {
                    onChapterDownload(it)
                    downloadChapter(provider, manga, it)
                },
                { onError(it) },
                { onAllChaptersDownloaded(manga) }
        )
    }

    @VisibleForTesting fun getChapters(provider: MangaProvider, manga: Manga): Observable<Chapter> {
        val chapters = provider.findChapters(manga)
        manga.chapters = chapters

        return from(chapters)
                .skip(alreadyDownloadedChapters(manga))
    }

    private fun downloadChapter(provider: MangaProvider, manga: Manga, chapter: Chapter) {
        getPages(provider, manga, chapter)
                .forEach (
                        {
                            onPageDownload(manga, chapter, it)
                            downloadPage(provider, manga, chapter, it)
                        },
                        { onError(it) },
                        { onAllPagesDownloaded(manga, chapter) }
                )
    }

    fun getPages(provider: MangaProvider, manga: Manga, chapter: Chapter): Observable<Page> {
        val pages = provider.findPages(chapter)
        chapter.pages = pages

        return from(pages)
    }

    private fun downloadPage(provider: MangaProvider, manga: Manga, chapter: Chapter, page: Page) {
        val inputStream = provider.findPage(page)

        fileHelper.getPageFile(manga, chapter, page).apply {
            createNewFile()
            HttpUtils.writeFile(inputStream, this)
            postProcess(provider, this)

        }

    }

    private fun onError(throwable: Throwable) {
        callback.onDownloadError(throwable)
    }

    @VisibleForTesting fun alreadyDownloadedChapters(manga: Manga): Int {
        if (manga.lastChapter == null || manga.lastChapter?.isEmpty()!!) {
            return manga.chapters?.size!! - 1
        } else {
            if ("all" == manga.lastChapter) {
                return 0
            } else {
                for (i in 0..manga.chapters?.size!! - 1) {
                    if (manga.chapters!!.get(i).chapterNumber == manga.lastChapter) {
                        return i + 1
                    }
                }
            }
        }

        return 0
    }

    private fun onAllMangasDownloaded() {
        Log.i("NewMangaDownloader", "All mangas downloaded")
        callback.onDownloadCompleted()
    }

    private fun onMangaDownload(manga: Manga) {
        Log.i("NewMangaDownloader", "Manga " + manga.name + " download")
    }

    private fun onAllChaptersDownloaded(manga: Manga) {
        Log.i("NewMangaDownloader", "All chapters downloaded")
        callback.onCompleteManga(manga)
    }

    private fun onChapterDownload(chapter: Chapter) {
        Log.i("NewMangaDownloader", "Chapter " + chapter.chapterNumber + " download")
    }

    private fun onAllPagesDownloaded(manga: Manga, chapter: Chapter) {
        Log.i("NewMangaDownloader", "All pages downloaded")
        callback.onCompleteChapter(manga, chapter)
    }

    private fun onPageDownload(manga: Manga, chapter: Chapter, page: Page) {
        Log.i("NewMangaDownloader", "Page " + page.pageNumber + "/" + chapter.pages?.size + " download")
        callback.onCompletePage(manga, chapter, page)
    }

    fun zipChapter(manga: Manga, chapter: Chapter) {

        Observable.create<Any> { subscriber ->
            val mangaFolder = fileHelper.getMangaFolder(manga)

            val zipFile = File("%s/%s - %s.cbz".format(mangaFolder.absoluteFile, manga.name, chapter.chapterNumber))

            try {
                FileOutputStream(zipFile).use { dest ->
                    ZipOutputStream(BufferedOutputStream(dest)).use { out ->

                        zipFile.createNewFile()
                        val chapterFolder = fileHelper.getChapterFolder(manga, chapter)

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
            } catch (e: IOException) {
                Log.e("MangaDownloadManager", "Error when zip chapter")

                if (zipFile.exists()) {
                    zipFile.delete()
                }
            } finally {
                subscriber.onCompleted()
            }
        }.subscribeOn(Schedulers.io()).subscribe()

    }

    fun postProcess(provider: MangaProvider, file: File) {
        Log.i("PostProcess", "post process : ${file.absolutePath}")
        provider.postProcess(file)
    }

    interface MangaDownloaderCallback {

        fun onDownloadError(throwable: Throwable)

        fun onDownloadCompleted()

        fun onCompleteManga(manga: Manga)

        fun onCompleteChapter(manga: Manga, chapter: Chapter)

        fun onCompletePage(manga: Manga, chapter: Chapter, page: Page)
    }
}
