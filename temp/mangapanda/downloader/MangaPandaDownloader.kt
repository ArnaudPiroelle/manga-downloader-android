package com.arnaudpiroelle.manga.provider.mangapanda.downloader

import android.support.annotation.VisibleForTesting
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.core.utils.HttpUtils
import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.model.network.Chapter
import com.arnaudpiroelle.manga.model.network.Page
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaApiService
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaDataApiService
import java.io.File
import java.io.InputStream
import java.net.URLDecoder
import java.util.*
import java.util.regex.Pattern

class MangaPandaDownloader(private val mangaPandaApiService: MangaPandaApiService, private val mangaPandaDataApiService: MangaPandaDataApiService) : MangaProvider {

    override fun findMangas(): List<Manga> {
        val mangaListResponse = mangaPandaApiService.getMangaList()

        val body = HttpUtils.convertFrom(mangaListResponse)

        return parseMangaList(body)
    }

    override fun findChapters(manga: Manga): List<Chapter> {
        val mangaResponse = mangaPandaApiService.getManga(manga.mangaAlias!!)

        val body = HttpUtils.convertFrom(mangaResponse)

        val list = parseMangaChapters(body)

        return list
    }

    override fun findPages(chapter: Chapter): List<Page> {
        val pageResponse = mangaPandaApiService.getReader(chapter.mangaAlias!!, chapter.chapterNumber!!)

        val body = HttpUtils.convertFrom(pageResponse)

        return parseMangaChapterPages(body)
    }

    override fun findPage(page: Page): InputStream {
        val pageResponse = mangaPandaApiService.getPage(page.mangaAlias!!, page.chapterNumber!!, page.pageNumber!!)

        val body = HttpUtils.convertFrom(pageResponse)
        val finalPage = parseMangaChapterPage(body)!!

        page.extension = finalPage.extension

        val downloadPageResponse = mangaPandaDataApiService.downloadPage(finalPage.mangaAlias!!, finalPage.chapterNumber!!, finalPage.pageName!!, finalPage.extension!!)

        return HttpUtils.readFrom(downloadPageResponse)!!
    }

    @VisibleForTesting
    fun parseMangaList(body: String): List<Manga> {
        val mangas = ArrayList<Manga>()

        val pattern = Pattern.compile("<li><a href=\"\\/([^\"]+)\">([^<]+)<\\/a>(<span class=\"mangacompleted\">\\[Completed]<\\/span>)?<\\/li>")
        val matcher = pattern.matcher(body)
        while (matcher.find()) {
            val url = URLDecoder.decode(matcher.group(1))
            val name = matcher.group(2)

            var manga: Manga = Manga();
            manga.name = name
            manga.mangaAlias = url
            manga.provider = this.name
            manga.lastChapter = ""
            mangas.add(manga)
        }

        return mangas
    }

    @VisibleForTesting
    fun parseMangaChapters(body: String): List<Chapter> {
        val chapters = ArrayList<Chapter>()

        val pattern = Pattern.compile("<td>([^<]*)<div class=\"chico_manga\"><\\/div>([^<]*)<a href=\"\\/([^\\/]*)\\/([^\"]*)\">([^<]*)<\\/a>([^<]*)<\\/td>")
        val matcher = pattern.matcher(body)
        while (matcher.find()) {
            val url = URLDecoder.decode(matcher.group(3))
            val chapterNumber = URLDecoder.decode(matcher.group(4))
            val name = matcher.group(5)

            var chapter: Chapter = Chapter()
            chapter.name = name
            chapter.mangaAlias = url
            chapter.chapterNumber = chapterNumber

            chapters.add(chapter)
        }

        return chapters
    }

    @VisibleForTesting
    fun parseMangaChapterPages(body: String): List<Page> {
        val pages = ArrayList<Page>()

        val pattern = Pattern.compile("<option value=\"\\/([^\\/]+)\\/([^\\/\"]+)(\\/([^\"]+))?\"( selected=\"selected\")?>([0-9]+)<\\/option>")
        val matcher = pattern.matcher(body)
        while (matcher.find()) {
            val url = URLDecoder.decode(matcher.group(1))
            val chapterNumber = URLDecoder.decode(matcher.group(2))
            val pageNumber = URLDecoder.decode(matcher.group(6))

            val page = Page()
            page.pageNumber = pageNumber
            page.mangaAlias = url
            page.chapterNumber = chapterNumber

            pages.add(page)
        }

        return pages
    }

    @VisibleForTesting
    fun parseMangaChapterPage(body: String): Page? {
        val pattern = Pattern.compile("src=\"http:\\/\\/([^.]+).mangacdn.com\\/([^\"]+)\\/([^\"]+)\\/([^\".]+).([^\"]+)\" alt=\"([^-]+)- Page ([^\"]+)")
        val matcher = pattern.matcher(body)
        while (matcher.find()) {
            val mangaAlias = URLDecoder.decode(matcher.group(2))
            val chapterNumber = URLDecoder.decode(matcher.group(3))
            val name = URLDecoder.decode(matcher.group(4))
            val pageNumber = URLDecoder.decode(matcher.group(7))
            val extension = URLDecoder.decode(matcher.group(5).toLowerCase())

            val page = Page()
            page.pageName = name
            page.pageNumber = pageNumber
            page.mangaAlias = mangaAlias
            page.chapterNumber = chapterNumber
            page.extension = extension

            return page
        }

        return null
    }

    override fun postProcess(file: File) {

    }
}
