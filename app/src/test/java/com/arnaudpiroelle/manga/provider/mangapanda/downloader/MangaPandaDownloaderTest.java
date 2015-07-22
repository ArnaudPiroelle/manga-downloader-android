package com.arnaudpiroelle.manga.provider.mangapanda.downloader;

import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.model.Page;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MangaPandaDownloaderTest {

    @InjectMocks
    MangaPandaDownloader mangaPandaDownloader;

    @Test
    public void should_extract_manga_from_html_body() throws Exception {
        // Given
        String html = "<li><a href=\"/00-mhz\">0.0 Mhz</a></li>";

        // When
        List<Manga> mangas = mangaPandaDownloader.parseMangaList(html);

        // Then
        assertThat(mangas).isNotNull();
        assertThat(mangas.size()).isEqualTo(1);

        Manga manga = mangas.get(0);
        assertThat(manga.getName()).isEqualTo("0.0 Mhz");
        assertThat(manga.getMangaAlias()).isEqualTo("00-mhz");
    }

    @Test
    public void should_extract_chapters_from_html() {
        // Given
        String html = "<tr>\n" +
                "                    <td>\n" +
                "                        <div class=\"chico_manga\"></div>\n" +
                "                        <a href=\"/naruto/1\">Naruto 1</a> : Uzumaki Naruto</td>\n" +
                "                    <td>07/04/2009</td>\n" +
                "                </tr>";

        // When
        List<Chapter> chapters = mangaPandaDownloader.parseMangaChapters(html);

        // Then`
        assertThat(chapters).isNotNull();
        assertThat(chapters.size()).isEqualTo(1);

        Chapter chapter = chapters.get(0);
        assertThat(chapter.getName()).isEqualTo("Naruto 1");
        assertThat(chapter.getMangaAlias()).isEqualTo("naruto");
        assertThat(chapter.getChapterNumber()).isEqualTo("1");
    }

    @Test
    public void should_extract_pages_from_html() {
        // Given
        String html = "<option value=\"/naruto/1\" selected=\"selected\">1</option>\n" +
                "                <option value=\"/naruto/1/2\">2</option>";

        // When
        List<Page> pages = mangaPandaDownloader.parseMangaChapterPages(html);

        // Then
        assertThat(pages).isNotNull();
        assertThat(pages.size()).isEqualTo(2);

        Page page = pages.get(0);
        assertThat(page.getMangaAlias()).isEqualTo("naruto");
        assertThat(page.getChapterNumber()).isEqualTo("1");
        assertThat(page.getPageNumber()).isEqualTo("1");

        page = pages.get(1);
        assertThat(page.getMangaAlias()).isEqualTo("naruto");
        assertThat(page.getChapterNumber()).isEqualTo("1");
        assertThat(page.getPageNumber()).isEqualTo("2");

    }

    @Test
    public void should_extract_page_from_reader() {
        // Given
        String html = "<div id=\"imgholder\"><a href=\"/naruto/1/2\"><img id=\"img\" width=\"800\" height=\"1263\" src=\"http://i10.mangacdn.com/naruto/1/naruto-1564773.jpg\" alt=\"Naruto 1 - Page 1\" name=\"img\" /></a> </div>";

        // When
        Page page = mangaPandaDownloader.parseMangaChapterPage(html);

        // Then
        assertThat(page).isNotNull();
        assertThat(page.getMangaAlias()).isEqualTo("naruto");
        assertThat(page.getChapterNumber()).isEqualTo("1");
        assertThat(page.getPageNumber()).isEqualTo("1");
        assertThat(page.getPageName()).isEqualTo("naruto-1564773");
        assertThat(page.getExtension()).isEqualTo("jpg");
    }
}