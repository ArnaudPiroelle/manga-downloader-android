package com.arnaudpiroelle.manga.provider.japscan.downloader

import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanApiService
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanDataApiService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class JapScanDownloaderTest {

    @Mock lateinit var japScanApiService: JapScanApiService
    @Mock lateinit var japScanDataApiService: JapScanDataApiService
    @InjectMocks lateinit var japScanDownloader: JapScanDownloader

    @Test
    fun should_extract_manga_from_html_body() {
        // Given
        val html = "<div class=\"row\"><div class=\"cell\"><a href=\"/mangas/090-eko-to-issho/\">090 Eko To Issho</a></div><div class=\"cell\">Shōnen</div><div class=\"cell\">En Cours</div><div class=\"cell\"><a href=\"/lecture-en-ligne/090-eko-to-issho/31/\">Vol 3 Chapitre 31</a></div></div>"

        // When
        val mangas = japScanDownloader.parseMangaList(html)

        // Then
        assertThat<Manga>(mangas).isNotNull()
        assertThat(mangas.size).isEqualTo(1)

        val manga = mangas.get(0)
        assertThat(manga.name).isEqualTo("090 Eko To Issho")
        assertThat(manga.mangaAlias).isEqualTo("090-eko-to-issho")
    }

    @Test
    fun should_extract_chapters_from_html() {
        // Given
        val html = "<li><a href=\"//www.japscan.com/lecture-en-ligne/090-eko-to-issho/9/\">Scan 090 Eko To Issho 9 VF</a></li>"

        // When
        val chapters = japScanDownloader.parseMangaChapters(html)

        // Then`
        assertThat<Chapter>(chapters).isNotNull()
        assertThat(chapters.size).isEqualTo(1)

        val chapter = chapters.get(0)
        assertThat(chapter.name).isEqualTo("Scan 090 Eko To Issho 9 VF")
        assertThat(chapter.mangaAlias).isEqualTo("090-eko-to-issho")
        assertThat(chapter.chapterNumber).isEqualTo("9")
    }

    @Test
    fun should_extract_pages_from_html() {
        // Given
        val html = ("<select name=\"mangas\" id=\"mangas\" data-nom=\"Toriko\" data-uri=\"toriko\"></select>" +
                "<select name=\"chapitres\" id=\"chapitres\" data-uri=\"396\"></select>" +
        "<select id=\"pages\" name=\"pages\">" +
        "<option selected=\"selected\" data-img=\"02.jpg\" value=\"/lecture-en-ligne/toriko/396/01.html\">Page 01</option>" +
        "<option data-img=\"04.jpg\" value=\"/lecture-en-ligne/toriko/396/02.html\">Page 02</option>").replace("\n".toRegex(), "").replace("\\t".toRegex(), "").replace("\\r".toRegex(), "")

        // When
        val pages = japScanDownloader.parseMangaChapterPages(html)

        // Then
        assertThat(pages).isNotNull()
        assertThat(pages.size).isEqualTo(2)

        val page = pages.get(0)
        assertThat(page.mangaAlias).isEqualTo("Toriko")
        assertThat(page.chapterNumber).isEqualTo("396")
        assertThat(page.pageNumber).isEqualTo("02")

    }

}