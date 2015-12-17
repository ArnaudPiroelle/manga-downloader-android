package com.arnaudpiroelle.manga.core.utils

import android.os.Environment

import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page

import java.io.File

import javax.inject.Inject

open class FileHelper {

    @Inject constructor() {}

    fun getEbooksFolder(): File {
        return File(Environment.getExternalStorageDirectory(), "eBooks")
    }

    fun getMangaFolder(manga: Manga): File {
        return File(getEbooksFolder(), manga.name)
    }

    fun getChapterFolder(manga: Manga, chapter: Chapter): File {
        return File(getMangaFolder(manga), chapter.chapterNumber)
    }

    fun getPageFile(manga: Manga, chapter: Chapter, page: Page): File {
        val pageFormated = "%03d.%s".format((chapter.pages!!.indexOf(page) + 1), page.extension)

        val chapterFolder = getChapterFolder(manga, chapter)
        chapterFolder.mkdirs()

        return File(chapterFolder, "/" + pageFormated)
    }
}
