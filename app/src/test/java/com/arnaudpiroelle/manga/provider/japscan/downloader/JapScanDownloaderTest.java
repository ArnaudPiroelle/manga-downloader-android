package com.arnaudpiroelle.manga.provider.japscan.downloader;

import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.model.Page;
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanApiService;
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanDataApiService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class JapScanDownloaderTest {

    @Mock
    JapScanApiService japScanApiService;

    @Mock
    JapScanDataApiService japScanDataApiService;

    @InjectMocks
    JapScanDownloader japScanDownloader;

    @Test
    public void should_extract_manga_from_html_body() throws Exception {
        // Given 
        String html = "<div class=\"row\"><div class=\"cell\"><a href=\"/mangas/090-eko-to-issho/\">090 Eko To Issho</a></div><div class=\"cell\">Shōnen</div><div class=\"cell\">En Cours</div><div class=\"cell\"><a href=\"/lecture-en-ligne/090-eko-to-issho/31/\">Vol 3 Chapitre 31</a></div></div>";

        // When
        List<Manga> mangas = japScanDownloader.parseMangaList(html);

        // Then
        assertThat(mangas).isNotNull();
        assertThat(mangas.size()).isEqualTo(1);

        Manga manga = mangas.get(0);
        assertThat(manga.getName()).isEqualTo("090 Eko To Issho");
        assertThat(manga.getMangaAlias()).isEqualTo("090-eko-to-issho");
    }

    @Test
    public void should_extract_chapters_from_html() {
        // Given
        String html = "<li><a href=\"//www.japscan.com/lecture-en-ligne/090-eko-to-issho/9/\">Scan 090 Eko To Issho 9 VF</a></li>";

        // When
        List<Chapter> chapters = japScanDownloader.parseMangaChapters(html);

        // Then`
        assertThat(chapters).isNotNull();
        assertThat(chapters.size()).isEqualTo(1);

        Chapter chapter = chapters.get(0);
        assertThat(chapter.getName()).isEqualTo("Scan 090 Eko To Issho 9 VF");
        assertThat(chapter.getMangaAlias()).isEqualTo("090-eko-to-issho");
        assertThat(chapter.getChapterNumber()).isEqualTo("9");
    }

    @Test
    public void should_extract_pages_from_html() {
        // Given
        String html = "<select id=\"pages\" name=\"pages\">" +
                ("<option value=\"/lecture-en-ligne/090-eko-to-issho/31/01.html\" selected=\"selected\">Page 01</option>" +
                        "<option value=\"/lecture-en-ligne/090-eko-to-issho/31/02.html\">Page 02</option>").replaceAll("\n", "")
                        .replaceAll("\\t", "")
                        .replaceAll("\\r", "");

        // When
        List<Page> pages = japScanDownloader.parseMangaChapterPages(html);

        // Then
        assertThat(pages).isNotNull();
        assertThat(pages.size()).isEqualTo(2);

        Page page = pages.get(0);
        assertThat(page.getMangaAlias()).isEqualTo("090-eko-to-issho");
        assertThat(page.getChapterNumber()).isEqualTo("31");
        assertThat(page.getPageNumber()).isEqualTo("01");

    }

    @Test
    public void should_extract_page_from_reader() {
        // Given
        String html = "<a id=\"imglink\" href=\"/lecture-en-ligne/toriko/205/2.html\">\n" +
                "\t\t\t\t<img width=\"1528px\" data-width=\"1528\" itemprop=\"image\" id=\"imgscan\" alt=\"Scan Toriko 205 VF page 1\" src=\"//cdn.japscan.com/lecture-en-ligne/Toriko/205/01%2002.jpg\" style=\"max-width: 1120px;\">\n" +
                "\t\t\t</a>";

        // When
        Page page = japScanDownloader.parseMangaChapterPage(html);

        // Then
        assertThat(page).isNotNull();
        assertThat(page.getMangaAlias()).isEqualTo("Toriko");
        assertThat(page.getChapterNumber()).isEqualTo("205");
        assertThat(page.getPageNumber()).isEqualTo("01 02");
        assertThat(page.getExtension()).isEqualTo("jpg");
    }

}