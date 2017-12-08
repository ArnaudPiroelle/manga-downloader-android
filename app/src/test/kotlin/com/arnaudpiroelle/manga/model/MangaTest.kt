package com.arnaudpiroelle.manga.model

import android.os.Parcel
import com.arnaudpiroelle.manga.BuildConfig
import com.arnaudpiroelle.manga.model.Chapter.ChapterBuilder.Companion.createChapter
import com.arnaudpiroelle.manga.model.Manga.MangaBuilder.Companion.createManga
import com.arnaudpiroelle.manga.model.Page.PageBuilder.Companion.createPage
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.util.Lists.newArrayList
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class MangaTest {

    @Test
    @Throws(Exception::class)
    fun should_compare_two_manga() {
        val manga1 = createManga().withName("bbb").build()
        val manga2 = createManga().withName("aaa").build()

        val compareTo = manga2.compareTo(manga1)

        assertThat(compareTo).isEqualTo(-1)

    }

    @Test
    @Throws(Exception::class)
    fun should_restore_manga_from_parcel() {
        // Given
        val manga = createManga().withId(1).withName("Title").withMangaAlias("Alias").withProvider("Provider").withLastChapter("Last Chapter").build()

        val chapter = createChapter().withName("Chapter").withMangaAlias("Alias").withChapterNumber("01").build()

        val page = createPage().withName("01").withChapterNumber("01").withMangaAlias("Alias").withPageNumber("01").withExtenstion("jpg").build()

        chapter.pages = newArrayList<Page>(page)
        manga.chapters = newArrayList<Chapter>(chapter)

        // When
        val parcel = Parcel.obtain()
        manga.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        // Then
        val mangaFromParcel = Manga.CREATOR.createFromParcel(parcel)

        assertThat(mangaFromParcel.id).isEqualTo(1)
        assertThat(mangaFromParcel.name).isEqualTo("Title")
        assertThat(mangaFromParcel.mangaAlias).isEqualTo("Alias")
        assertThat(mangaFromParcel.provider).isEqualTo("Provider")
        assertThat(mangaFromParcel.lastChapter).isEqualTo("Last Chapter")
        assertThat(mangaFromParcel.chapters!!.size).isEqualTo(1)

        val chapterFromParcel = mangaFromParcel.chapters!![0]
        assertThat(chapterFromParcel.name).isEqualTo("Chapter")
        assertThat(chapterFromParcel.mangaAlias).isEqualTo("Alias")
        assertThat(chapterFromParcel.chapterNumber).isEqualTo("01")
        assertThat(chapterFromParcel.pages!!.size).isEqualTo(1)

        val pageFromParcel = chapterFromParcel.pages!![0]
        assertThat(pageFromParcel.pageName).isEqualTo("01")
        assertThat(pageFromParcel.chapterNumber).isEqualTo("01")
        assertThat(pageFromParcel.mangaAlias).isEqualTo("Alias")
        assertThat(pageFromParcel.pageNumber).isEqualTo("01")
        assertThat(pageFromParcel.extension).isEqualTo("jpg")

    }
}