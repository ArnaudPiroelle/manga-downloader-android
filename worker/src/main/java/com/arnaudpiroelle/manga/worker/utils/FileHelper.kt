package com.arnaudpiroelle.manga.worker.utils

import android.os.Environment.getExternalStorageDirectory
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.data.model.Page
import java.io.File

open class FileHelper(private val preferencesHelper: PreferencesHelper) {

    private fun getEbooksFolder(): File {
        val outputFolder = preferencesHelper.getOutputFolder()

        return if (outputFolder != null) {
            File(outputFolder)
        } else {
            File(getExternalStorageDirectory(), "eBooks")
        }
    }

    fun getMangaFolder(manga: Manga): File {
        return File(getEbooksFolder(), manga.cleanName())
    }

    fun getChapterFolder(manga: Manga, chapter: Chapter): File {
        return File(getMangaFolder(manga), "${manga.cleanName()} - ${chapter.number}")
    }

    fun getChapterCBZFile(manga: Manga, chapter: Chapter): File {
        return File(getMangaFolder(manga), "${manga.cleanName()} - ${chapter.number}.cbz")
    }

    fun getPageFile(manga: Manga, chapter: Chapter, page: Page, pagePosition: Int): File {
        val pageFormated = "%03d.%s".format((pagePosition + 1), page.getExtension())

        val chapterFolder = getChapterFolder(manga, chapter)
        chapterFolder.mkdirs()

        return File(chapterFolder, pageFormated)
    }
}
