package com.arnaudpiroelle.manga.provider.mangapanda.downloader

import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanApiService
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanDataApiService
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaApiService
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaDataApiService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MangaPandaDownloaderTest {

    @Mock lateinit var mangaPandaApiService: MangaPandaApiService
    @Mock lateinit var mangaPandaDataApiService: MangaPandaDataApiService
    @InjectMocks lateinit var mangaPandaDownloader: MangaPandaDownloader

    @Test
    fun should_extract_manga_from_html_body() {
        // Given
        val html = "<li><a href=\"/00-mhz\">0.0 Mhz</a></li>"

        // When
        val mangas = mangaPandaDownloader.parseMangaList(html)

        // Then
        assertThat<Manga>(mangas).isNotNull()
        assertThat(mangas.size).isEqualTo(1)

        val manga = mangas.get(0)
        assertThat(manga.name).isEqualTo("0.0 Mhz")
        assertThat(manga.mangaAlias).isEqualTo("00-mhz")
    }

    @Test
    fun should_extract_chapters_from_html() {
        // Given
        val html = "<tr>\n" + "                    <td>\n" + "                        <div class=\"chico_manga\"></div>\n" + "                        <a href=\"/naruto/1\">Naruto 1</a> : Uzumaki Naruto</td>\n" + "                    <td>07/04/2009</td>\n" + "                </tr>"

        // When
        val chapters = mangaPandaDownloader.parseMangaChapters(html)

        // Then`
        assertThat<Chapter>(chapters).isNotNull()
        assertThat(chapters.size).isEqualTo(1)

        val chapter = chapters.get(0)
        assertThat(chapter.name).isEqualTo("Naruto 1")
        assertThat(chapter.mangaAlias).isEqualTo("naruto")
        assertThat(chapter.chapterNumber).isEqualTo("1")
    }

    @Test
    fun should_extract_pages_from_html() {
        // Given
        val html = "<option value=\"/naruto/1\" selected=\"selected\">1</option>\n" + "                <option value=\"/naruto/1/2\">2</option>"

        // When
        val pages = mangaPandaDownloader.parseMangaChapterPages(html)

        // Then
        assertThat<Page>(pages).isNotNull()
        assertThat(pages.size).isEqualTo(2)

        var page = pages.get(0)
        assertThat(page.mangaAlias).isEqualTo("naruto")
        assertThat(page.chapterNumber).isEqualTo("1")
        assertThat(page.pageNumber).isEqualTo("1")

        page = pages.get(1)
        assertThat(page.mangaAlias).isEqualTo("naruto")
        assertThat(page.chapterNumber).isEqualTo("1")
        assertThat(page.pageNumber).isEqualTo("2")

    }

    @Test
    fun should_extract_page_from_reader() {
        // Given
        val html = "<div id=\"imgholder\"><a href=\"/naruto/1/2\"><img id=\"img\" width=\"800\" height=\"1263\" src=\"http://i10.mangacdn.com/naruto/1/naruto-1564773.jpg\" alt=\"Naruto 1 - Page 1\" name=\"img\" /></a> </div>"

        // When
        val page = mangaPandaDownloader.parseMangaChapterPage(html)!!

        // Then
        assertThat(page).isNotNull()
        assertThat(page.mangaAlias).isEqualTo("naruto")
        assertThat(page.chapterNumber).isEqualTo("1")
        assertThat(page.pageNumber).isEqualTo("1")
        assertThat(page.pageName).isEqualTo("naruto-1564773")
        assertThat(page.extension).isEqualTo("jpg")
    }
}