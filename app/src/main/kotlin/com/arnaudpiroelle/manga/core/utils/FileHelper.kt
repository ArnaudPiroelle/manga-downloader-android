package com.arnaudpiroelle.manga.core.utils

import android.os.Environment.getExternalStorageDirectory
import com.arnaudpiroelle.manga.api.model.Chapter
import com.arnaudpiroelle.manga.api.model.Page
import com.arnaudpiroelle.manga.model.db.Manga
import java.io.File
import javax.inject.Inject

open class FileHelper @Inject constructor(val preferencesHelper: PreferencesHelper) {

    private fun getEbooksFolder(): File {
        val outputFolder = preferencesHelper.getOutputFolder()

        return if (outputFolder != null) {
            File(outputFolder)
        } else {
            File(getExternalStorageDirectory(), "eBooks")
        }
    }

    fun getMangaFolder(manga: Manga): File {
        return File(getEbooksFolder(), manga.name)
    }

    fun getChapterFolder(manga: Manga, chapter: Chapter): File {
        return File(getMangaFolder(manga), chapter.chapterNumber)
    }

    fun getPageFile(manga: Manga, chapter: Chapter, page: Page, pagePosition: Int): File {
        val pageFormated = "%03d.%s".format((pagePosition + 1), page.getExtension())

        val chapterFolder = getChapterFolder(manga, chapter)
        chapterFolder.mkdirs()

        return File(chapterFolder, pageFormated)
    }
}
