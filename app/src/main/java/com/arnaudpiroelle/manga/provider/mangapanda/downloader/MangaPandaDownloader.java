package com.arnaudpiroelle.manga.provider.mangapanda.downloader;

import android.support.annotation.VisibleForTesting;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.utils.HttpUtils;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.model.Page;
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaApiService;
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaDataApiService;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import retrofit.client.Response;

import static com.arnaudpiroelle.manga.model.Chapter.ChapterBuilder.createChapter;
import static com.arnaudpiroelle.manga.model.Manga.MangaBuilder.createManga;
import static com.arnaudpiroelle.manga.model.Page.PageBuilder.createPage;

public class MangaPandaDownloader implements MangaProvider{

    private MangaPandaApiService mangaPandaApiService;
    private MangaPandaDataApiService mangaPandaDataApiService;

    @Inject
    public MangaPandaDownloader(MangaPandaApiService mangaPandaApiService, MangaPandaDataApiService mangaPandaDataApiService) {
        this.mangaPandaApiService = mangaPandaApiService;
        this.mangaPandaDataApiService = mangaPandaDataApiService;
    }

    @Override
    public List<Manga> findMangas() {
        Response mangaListResponse = mangaPandaApiService.getMangaList();

        String body = HttpUtils.convertFrom(mangaListResponse);

        return parseMangaList(body);
    }

    @Override
    public List<Chapter> findChapters(Manga manga) {
        Response mangaResponse = mangaPandaApiService.getManga(manga.getMangaAlias());

        String body = HttpUtils.convertFrom(mangaResponse);

        List<Chapter> list = parseMangaChapters(body);

        return list;
    }

    @Override
    public List<Page> findPages(Chapter chapter) {
        Response pageResponse = mangaPandaApiService.getReader(chapter.getMangaAlias(), chapter.getChapterNumber());

        String body = HttpUtils.convertFrom(pageResponse);

        List<Page> pages = parseMangaChapterPages(body);

        return pages;
    }

    @Override
    public InputStream findPage(Page page) {
        Response pageResponse = mangaPandaApiService.getPage(page.getMangaAlias(), page.getChapterNumber(), page.getPageNumber());

        String body = HttpUtils.convertFrom(pageResponse);
        Page finalPage = parseMangaChapterPage(body);

        page.setExtension(finalPage.getExtension());

        Response downloadPageResponse = mangaPandaDataApiService.downloadPage(finalPage.getMangaAlias(), finalPage.getChapterNumber(), finalPage.getPageName(), finalPage.getExtension());

        return HttpUtils.readFrom(downloadPageResponse);
    }

    @Override
    public String getName() {
        return "MangaPanda";
    }

    @VisibleForTesting List<Manga> parseMangaList(String body) {
        List<Manga> mangas = new ArrayList<Manga>();

        Pattern pattern = Pattern.compile("<li><a href=\"\\/([^\"]+)\">([^<]+)<\\/a>(<span class=\"mangacompleted\">\\[Completed]<\\/span>)?<\\/li>");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            String url = URLDecoder.decode(matcher.group(1));
            String name = matcher.group(2);

            Manga manga = createManga()
                    .withName(name)
                    .withMangaAlias(url)
                    .withProvider(getName())
                    .withLastChapter("")
                    .build();

            mangas.add(manga);
        }

        return mangas;
    }

    @VisibleForTesting List<Chapter> parseMangaChapters(String body) {
        List<Chapter> chapters = new ArrayList<Chapter>();

        Pattern pattern = Pattern.compile("<td>([^<]*)<div class=\"chico_manga\"><\\/div>([^<]*)<a href=\"\\/([^\\/]*)\\/([^\"]*)\">([^<]*)<\\/a>([^<]*)<\\/td>");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            String url = URLDecoder.decode(matcher.group(3));
            String chapterNumber = URLDecoder.decode(matcher.group(4));
            String name = matcher.group(5);

            Chapter chapter = createChapter()
                    .withName(name)
                    .withMangaAlias(url)
                    .withChapterNumber(chapterNumber)
                    .build();

            chapters.add(chapter);
        }

        return chapters;
    }

    @VisibleForTesting List<Page> parseMangaChapterPages(String body) {
        List<Page> pages = new ArrayList<Page>();

        Pattern pattern = Pattern.compile("<option value=\"\\/([^\\/]+)\\/([^\\/\"]+)(\\/([^\"]+))?\"( selected=\"selected\")?>([0-9]+)<\\/option>");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            String url = URLDecoder.decode(matcher.group(1));
            String chapterNumber = URLDecoder.decode(matcher.group(2));
            String pageNumber = URLDecoder.decode(matcher.group(6));

            Page page = createPage()
                    .withPageNumber(pageNumber)
                    .withMangaAlias(url)
                    .withChapterNumber(chapterNumber)
                    .build();

            pages.add(page);
        }

        return pages;
    }

    @VisibleForTesting Page parseMangaChapterPage(String body) {
        Pattern pattern = Pattern.compile("src=\"http:\\/\\/([^.]+).mangacdn.com\\/([^\"]+)\\/([^\"]+)\\/([^\".]+).([^\"]+)\" alt=\"([^-]+)- Page ([^\"]+)");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            String mangaAlias = URLDecoder.decode(matcher.group(2));
            String chapterNumber = URLDecoder.decode(matcher.group(3));
            String name = URLDecoder.decode(matcher.group(4));
            String pageNumber = URLDecoder.decode(matcher.group(7));
            String extension = URLDecoder.decode(matcher.group(5).toLowerCase());

            Page page = createPage()
                    .withName(name)
                    .withPageNumber(pageNumber)
                    .withMangaAlias(mangaAlias)
                    .withChapterNumber(chapterNumber)
                    .withExtenstion(extension)
                    .build();

            return page;
        }

        return null;
    }
}
