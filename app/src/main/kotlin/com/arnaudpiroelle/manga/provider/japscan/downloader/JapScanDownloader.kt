package com.arnaudpiroelle.manga.provider.japscan.downloader


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.support.annotation.VisibleForTesting
import android.util.Log
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.core.utils.HttpUtils
import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanApiService
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanDataApiService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URLDecoder
import java.util.*
import java.util.regex.Pattern

class JapScanDownloader(private val japScanApiService: JapScanApiService,
                        private val japScanDataApiService: JapScanDataApiService) : MangaProvider {

    override fun findMangas(): List<Manga> {
        val mangaListResponse = japScanApiService.getMangaList()

        val body = HttpUtils.convertFrom(mangaListResponse)

        return parseMangaList(body)
    }

    override fun findChapters(manga: Manga): List<Chapter> {
        val mangaResponse = japScanApiService.getManga(manga.mangaAlias!!)

        val body = HttpUtils.convertFrom(mangaResponse)

        val list = parseMangaChapters(body)
        Collections.reverse(list)

        return list
    }

    override fun findPages(chapter: Chapter): List<Page> {
        val pageResponse = japScanApiService.getReader(chapter.mangaAlias!!, chapter.chapterNumber!!)

        val body = HttpUtils.convertFrom(pageResponse)

        val pages = parseMangaChapterPages(body)

        return pages
    }

    override fun findPage(page: Page): InputStream {

        val downloadPageResponse = japScanDataApiService.downloadPage(page.mangaAlias!!, page.chapterNumber!!, page.pageNumber!!, page.extension!!)

        return HttpUtils.readFrom(downloadPageResponse)!!
    }

    override val name: String
        get() = "JapScan"

    @VisibleForTesting fun parseMangaList(body: String): List<Manga> {
        val mangas = ArrayList<Manga>()

        val pattern = Pattern.compile("<div class=\"row\"><div class=\"cell\"><a href=\"\\/mangas\\/([^\"]+)\\/\">([^<]+)<\\/a><\\/div><div class=\"cell\">([^<]+)<\\/div><div class=\"cell\">([^<]+)<\\/div><div class=\"cell\"><a href=\"([^\"]+)\">([^<]+)<\\/a><\\/div><\\/div>")
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

    @VisibleForTesting fun parseMangaChapters(body: String): List<Chapter> {
        val chapters = ArrayList<Chapter>()

        val pattern = Pattern.compile("<li><a href=\"\\/\\/www.japscan.com\\/lecture-en-ligne\\/([^\"]+)\\/([^\"]*)\\/\">([^<]*)<\\/a><\\/li>")
        val matcher = pattern.matcher(body)
        while (matcher.find()) {
            val url = URLDecoder.decode(matcher.group(1))
            val chapterNumber = URLDecoder.decode(matcher.group(2))
            val name = matcher.group(3)

            var chapter: Chapter = Chapter()
            chapter.name = name
            chapter.mangaAlias = url
            chapter.chapterNumber = chapterNumber

            chapters.add(chapter)
        }

        return chapters
    }

    @VisibleForTesting fun parseMangaChapterPages(body: String): List<Page> {
        val pages = ArrayList<Page>()

        val mangaPattern = Pattern.compile("<select name=\"mangas\" id=\"mangas\" data-nom=\"([^\"]+)\" data-uri=\"([^\"]+)\"><\\/select>")
        val chapterPattern = Pattern.compile("<select name=\"chapitres\" id=\"chapitres\" data-uri=\"([^\"]+)\"( data-nom=\"([^\"]+)\")?><\\/select>")
        val pagesPattern = Pattern.compile("<option( selected=\"selected\")? data-img=\"([^\"]+)\" value=\"\\/lecture-en-ligne\\/([^\"]+)\\/([^\"]+)\\/([^\"]+).html\">Page ([0-9]+)<\\/option>")

        val mangaMatcher = mangaPattern.matcher(body)
        if (mangaMatcher.find()) {
            val mangaNom = URLDecoder.decode(mangaMatcher.group(1))

            val chapterMatcher = chapterPattern.matcher(body)
            if (chapterMatcher.find()) {
                val chapterUri = URLDecoder.decode(chapterMatcher.group(1))

                var chapterNom: String? = null
                if (chapterMatcher.groupCount() > 2) {
                    chapterNom = chapterMatcher.group(3)
                }

                val pagesMatcher = pagesPattern.matcher(body)
                while (pagesMatcher.find()) {

                    val pageNumber = if (pagesMatcher.groupCount() == 5) URLDecoder.decode(pagesMatcher.group(1)) else URLDecoder.decode(pagesMatcher.group(2))

                    val lastIndex = pageNumber.lastIndexOf(".")

                    var page: Page = Page()
                    page.pageNumber = pageNumber.substring(0, lastIndex)
                    page.mangaAlias = mangaNom
                    page.chapterNumber = if (chapterNom != null) chapterNom else chapterUri
                    page.extension = pageNumber.substring(lastIndex + 1)

                    pages.add(page)
                }
            }
        }

        return pages
    }

    override fun postProcess(file: File) {
        val source = BitmapFactory.decodeFile(file.absolutePath)

        val newBitmap = Bitmap.createBitmap(source.width, source.height, source.config)
        val comboImage = Canvas(newBitmap)

        try {
            val width = source.width
            val height = source.height

            val w_p = Math.floor((width / 5).toDouble())
            val h_p = Math.floor((height / 5).toDouble())
            val x = doubleArrayOf(w_p * 2, w_p * 4, 0.0, w_p * 3, w_p)
            val y = doubleArrayOf(h_p * 4, h_p * 3, h_p * 2, h_p, 0.0)

            var p_h: Double?
            var p_w: Double?
            for (i in 1..5) {
                p_h = y[i - 1]

                for (j in 1..5) {
                    p_w = x[j - 1]
                    val leftDst = (j - 1) * w_p
                    val topDst = (i - 1) * h_p
                    val rightDst = j * w_p
                    val bottomDst = i * h_p

                    val leftSrc = p_w
                    val topSrc = p_h
                    val rightSrc = p_w + w_p
                    val bottomSrc = p_h + h_p

                    val src = Rect(leftSrc.toInt(), topSrc.toInt(), rightSrc.toInt(), bottomSrc.toInt())
                    val dst = Rect(leftDst.toInt(), topDst.toInt(), rightDst.toInt(), bottomDst.toInt())

                    comboImage.drawBitmap(source, src, dst, null)

                }
            }


            FileOutputStream(file).use { os ->
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()
            }
        } catch (e: Exception) {
            Log.e("combineImages", "problem combining images", e)
        }

        source.recycle()
    }
}
