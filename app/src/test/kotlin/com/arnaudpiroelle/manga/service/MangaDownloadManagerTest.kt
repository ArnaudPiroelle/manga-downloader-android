package com.arnaudpiroelle.manga.service

import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.provider.ProviderRegistryBuilder
import com.arnaudpiroelle.manga.core.utils.FileHelper
import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Chapter.ChapterBuilder.Companion.createChapter
import com.arnaudpiroelle.manga.model.Manga.MangaBuilder.Companion.createManga
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.util.Lists
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MangaDownloadManagerTest {

    val provider = Mockito.mock(MangaProvider::class.java)
    lateinit var providerRegistry: ProviderRegistry
    lateinit var mangaDownloadManager: MangaDownloadManager

    @Before
    fun setUp() {
        providerRegistry = ProviderRegistryBuilder.createProviderRegister().withProvider(provider).build()
        mangaDownloadManager = MangaDownloadManager(
                Mockito.mock(MangaDownloadManager.MangaDownloaderCallback::class.java),
                providerRegistry,
                Mockito.mock(FileHelper::class.java)
        )
    }

    @Test
    fun should_return_last_chapter_if_manga_has_empty_last_chapter() {
        // Given
        val manga = createManga().build()
        val chapter1 = createChapter().withChapterNumber("1").build()
        val chapter2 = createChapter().withChapterNumber("2").build()
        manga.chapters = Lists.newArrayList<Chapter>(chapter1, chapter2)

        // When
        val result = mangaDownloadManager.alreadyDownloadedChapters(manga)

        // Then
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun should_return_0_if_manga_has_all_value_for_last_chapter() {
        // Given
        val manga = createManga().withLastChapter("all").build()

        // When
        val result = mangaDownloadManager.alreadyDownloadedChapters(manga)

        // Then
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun should_return_correct_position_if_manga_has_a_last_chapter() {
        // Given
        val manga = createManga().withLastChapter("3").build()
        val chapter1 = createChapter().withChapterNumber("1").build()
        val chapter2 = createChapter().withChapterNumber("2").build()
        val chapter3 = createChapter().withChapterNumber("3").build()
        val chapter4 = createChapter().withChapterNumber("4").build()
        manga.chapters = Lists.newArrayList<Chapter>(chapter1, chapter2, chapter3, chapter4)

        // When
        val result = mangaDownloadManager.alreadyDownloadedChapters(manga)

        // Then
        assertThat(result).isEqualTo(3)
    }

    @Test
    fun should_return_all_chapters_of_manga() {
        // Given
        val manga = createManga().withName("manga1").withLastChapter("all").withProvider("provider1").build()

        val chapter1 = createChapter().withName("Chapter 1").withMangaAlias("manga1").withChapterNumber("01").build()
        val chapter2 = createChapter().withName("Chapter 2").withMangaAlias("manga1").withChapterNumber("02").build()

        val provider = Mockito.mock(MangaProvider::class.java)
        `when`(provider.findChapters(manga)).thenReturn(Lists.newArrayList<Chapter>(chapter1, chapter2))

        // When
        val chapters = mangaDownloadManager.getChapters(provider, manga).toList().toBlocking().single()

        // Then
        assertThat<Chapter>(manga.chapters).containsAll(chapters)
        assertThat(chapters.size).isEqualTo(2)

    }

    @Test
    fun should_return_last_chapters_of_manga() {
        // Given
        val manga = createManga().withName("manga1").withLastChapter("").withProvider("provider1").build()

        val chapter1 = createChapter().withName("Chapter 1").withMangaAlias("manga1").withChapterNumber("01").build()
        val chapter2 = createChapter().withName("Chapter 2").withMangaAlias("manga1").withChapterNumber("02").build()

        val provider = Mockito.mock(MangaProvider::class.java)
        Mockito.`when`(provider.findChapters(manga)).thenReturn(Lists.newArrayList<Chapter>(chapter1, chapter2))

        // When
        val chapters = mangaDownloadManager.getChapters(provider, manga).toList().toBlocking().single()

        // Then
        assertThat(manga.chapters).containsAll(chapters)
        assertThat(chapters.size).isEqualTo(1)
        assertThat(chapters.get(0)).isEqualTo(chapter2)

    }

    @Test
    fun should_return_only_new_chapters_of_manga() {
        // Given
        val manga = createManga().withName("manga1").withLastChapter("01").withProvider("provider1").build()

        val chapter1 = createChapter().withName("Chapter 1").withMangaAlias("manga1").withChapterNumber("01").build()
        val chapter2 = createChapter().withName("Chapter 2").withMangaAlias("manga1").withChapterNumber("02").build()
        val chapter3 = createChapter().withName("Chapter 3").withMangaAlias("manga1").withChapterNumber("03").build()

        val provider = Mockito.mock(MangaProvider::class.java)
        `when`(provider.findChapters(manga)).thenReturn(Lists.newArrayList<Chapter>(chapter1, chapter2, chapter3))

        // When
        val chapters = mangaDownloadManager.getChapters(provider, manga).toList().toBlocking().single()

        // Then
        assertThat<Chapter>(manga.chapters).containsAll(chapters)
        assertThat(chapters.size).isEqualTo(2)
        assertThat<Chapter>(chapters).containsAll(Lists.newArrayList<Chapter>(chapter2, chapter3))

    }
}