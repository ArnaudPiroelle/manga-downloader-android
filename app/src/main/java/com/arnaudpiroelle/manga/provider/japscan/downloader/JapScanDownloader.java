package com.arnaudpiroelle.manga.provider.japscan.downloader;


import android.support.annotation.VisibleForTesting;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.utils.HttpUtils;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.model.Page;
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanApiService;
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanDataApiService;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.client.Response;

import static com.arnaudpiroelle.manga.model.Chapter.ChapterBuilder.createChapter;
import static com.arnaudpiroelle.manga.model.Manga.MangaBuilder.createManga;
import static com.arnaudpiroelle.manga.model.Page.PageBuilder.createPage;

public class JapScanDownloader implements MangaProvider {

    private JapScanApiService japScanApiService;
    private JapScanDataApiService japScanDataApiService;

    public JapScanDownloader(JapScanApiService japScanApiService, JapScanDataApiService japScanDataApiService) {
        this.japScanApiService = japScanApiService;
        this.japScanDataApiService = japScanDataApiService;
    }

    public List<Manga> findMangas() {
        Response mangaListResponse = japScanApiService.getMangaList();

        String body = HttpUtils.convertFrom(mangaListResponse);

        return parseMangaList(body);
    }

    public List<Chapter> findChapters(Manga manga) {
        Response mangaResponse = japScanApiService.getManga(manga.getMangaAlias());

        String body = HttpUtils.convertFrom(mangaResponse);

        List<Chapter> list = parseMangaChapters(body);
        Collections.reverse(list);

        return list;
    }

    public List<Page> findPages(Chapter chapter) {
        Response pageResponse = japScanApiService.getReader(chapter.getMangaAlias(), chapter.getChapterNumber());

        String body = HttpUtils.convertFrom(pageResponse);

        List<Page> pages = parseMangaChapterPages(body);

        return pages;
    }

    public InputStream findPage(Page page) {
        Response pageResponse = japScanApiService.getPage(page.getMangaAlias(), page.getChapterNumber(), page.getPageNumber());

        String body = HttpUtils.convertFrom(pageResponse);
        Page finalPage = parseMangaChapterPage(body);

        page.setExtension(finalPage.getExtension());

        Response downloadPageResponse = japScanDataApiService.downloadPage(finalPage.getMangaAlias(), finalPage.getChapterNumber(), finalPage.getPageNumber(), finalPage.getExtension());

        return HttpUtils.readFrom(downloadPageResponse);
    }

    @Override
    public String getName() {
        return "JapScan";
    }

    @VisibleForTesting List<Manga> parseMangaList(String body) {
        List<Manga> mangas = new ArrayList<Manga>();

        Pattern pattern = Pattern.compile("<div class=\"row\"><div class=\"cell\"><a href=\"\\/mangas\\/([^\"]+)\\/\">([^<]+)<\\/a><\\/div><div class=\"cell\">([^<]+)<\\/div><div class=\"cell\">([^<]+)<\\/div><div class=\"cell\"><a href=\"([^\"]+)\">([^<]+)<\\/a><\\/div><\\/div>");
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

        Pattern pattern = Pattern.compile("<li><a href=\"\\/\\/www.japscan.com\\/lecture-en-ligne\\/([^\"]+)\\/([^\"]*)\\/\">([^<]*)<\\/a><\\/li>");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            String url = URLDecoder.decode(matcher.group(1));
            String chapterNumber = URLDecoder.decode(matcher.group(2));
            String name = matcher.group(3);

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

        Pattern pattern = Pattern.compile("<option value=\"\\/lecture-en-ligne\\/([^\"]+)\\/([^\"]+)\\/([^\"]+).html\"( selected=\"selected\")?>Page ([0-9]+)<\\/option>");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            String url = URLDecoder.decode(matcher.group(1));
            String chapterNumber = URLDecoder.decode(matcher.group(2));
            String pageNumber = URLDecoder.decode(matcher.group(3));

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
        Pattern pattern = Pattern.compile("src=\"\\/\\/cdn.japscan.com\\/lecture-en-ligne\\/([^\"]+)\\/([^\"]+)\\/([^\".]+).([^\"]+)\"");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            String mangaAlias = URLDecoder.decode(matcher.group(1));
            String chapterNumber = URLDecoder.decode(matcher.group(2));
            String pageNumber = URLDecoder.decode(matcher.group(3));
            String extension = URLDecoder.decode(matcher.group(4).toLowerCase());

            Page page = createPage()
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
