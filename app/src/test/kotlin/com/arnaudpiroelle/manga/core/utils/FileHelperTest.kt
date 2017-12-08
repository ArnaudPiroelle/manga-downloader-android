package com.arnaudpiroelle.manga.core.utils

import android.os.Environment
import com.arnaudpiroelle.manga.BuildConfig
import com.arnaudpiroelle.manga.model.Chapter.ChapterBuilder.Companion.createChapter
import com.arnaudpiroelle.manga.model.Manga.MangaBuilder.Companion.createManga
import com.arnaudpiroelle.manga.model.Page.PageBuilder.Companion.createPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class FileHelperTest {

    lateinit var fileHelper: FileHelper

    @Before
    @Throws(Exception::class)
    fun setUp() {
        fileHelper = FileHelper()
    }

    @Test
    fun should_compute_ebooks_folder() {
        // Given / When
        val ebooksFolder = fileHelper.getEbooksFolder()

        // Then
        assertThat(ebooksFolder.absolutePath).isEqualTo(Environment.getExternalStorageDirectory().absolutePath + "/eBooks")

    }

    @Test
    fun should_compute_manga_folder() {
        // Given
        val manga = createManga().withName("My Manga").build()

        // When
        val ebooksFolder = fileHelper.getMangaFolder(manga)

        // Then
        assertThat(ebooksFolder.absolutePath).isEqualTo(Environment.getExternalStorageDirectory().absolutePath + "/eBooks/My Manga")

    }

    @Test
    fun should_compute_chapter_folder() {
        // Given
        val manga = createManga().withName("My Manga").build()
        val chapter = createChapter().withChapterNumber("01").build()

        // When
        val ebooksFolder = fileHelper.getChapterFolder(manga, chapter)

        // Then
        assertThat(ebooksFolder.absolutePath).isEqualTo(Environment.getExternalStorageDirectory().absolutePath + "/eBooks/My Manga/01")

    }

    @Test
    fun should_compute_page_file() {
        // Given
        val manga = createManga().withName("My Manga").build()
        val chapter = createChapter().withChapterNumber("01").build()
        val page = createPage().withExtenstion("jpg").withPageNumber("1").build()

        chapter.pages = arrayListOf(page)

        // When
        val ebooksFolder = fileHelper.getPageFile(manga, chapter, page)

        // Then
        assertThat(ebooksFolder.absolutePath).isEqualTo(Environment.getExternalStorageDirectory().absolutePath + "/eBooks/My Manga/01/001.jpg")

    }
}