package com.arnaudpiroelle.manga.provider.japscan.downloader


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.arnaudpiroelle.manga.BuildConfig
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.core.rx.RxRequest
import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.model.network.Chapter
import com.arnaudpiroelle.manga.model.network.Page
import com.arnaudpiroelle.manga.model.network.PostProcess
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder


class JapScanDownloader(private val okHttpClient: OkHttpClient) : MangaProvider {
    override fun findMangas(): Single<List<Manga>> {
        val request = Request.Builder()
                .url("${BuildConfig.JAPSCAN_BASE_URL}/mangas/")
                .build()

        return RxRequest(okHttpClient, request)
                .map(this::parseMangas)
                .onErrorReturn { listOf() }
    }

    override fun findChapters(manga: Manga): Single<List<Chapter>> {
        val request = Request.Builder()
                .url("${BuildConfig.JAPSCAN_BASE_URL}/mangas/${manga.mangaAlias}")
                .build()

        return RxRequest(okHttpClient, request)
                .map(this::parseChapters)
                .map { it.reversed() }
                .onErrorReturn { listOf() }

    }

    override fun findPages(manga: Manga, chapter: Chapter): Single<List<Page>> {
        val request = Request.Builder()
                .url("${BuildConfig.JAPSCAN_BASE_URL}/lecture-en-ligne/${manga.mangaAlias}/${chapter.chapterNumber}")
                .build()

        return RxRequest(okHttpClient, request)
                .map(this::parsePages)
                .onErrorReturn { listOf() }
    }

    override fun findPage(page: Page): Single<Response> {
        val request = Request.Builder()
                .url(page.url)
                .build()

        return RxRequest(okHttpClient, request)
    }

    private fun parseMangas(response: Response): List<Manga> {
        val doc = Jsoup.parse(response.body()?.string())
        val mangaElements = doc.select("div.cell>a[href*=/mangas/]")

        return mangaElements.map {
            val url = it.attr("href")
            val name = it.text()

            Manga(name, url.substringAfter("/mangas/"), "JapScan")
        }
    }

    private fun parseChapters(response: Response): List<Chapter> {
        val doc = Jsoup.parse(response.body()?.string())

        val chapters = doc.select("#liste_chapitres > ul > li:not(:has(span)) > a")
        return chapters.map {

            val url = it.attr("href")
            val name = it.text()

            val parts = url.split("/")
            val chapterNumber = URLDecoder.decode(parts[parts.lastIndex - 1], "UTF-8")
            val mangaAlias = URLDecoder.decode(parts[parts.lastIndex - 2], "UTF-8")

            Chapter(name, mangaAlias, chapterNumber)
        }

    }

    private fun parsePages(response: Response): List<Page> {
        val doc = Jsoup.parse(response.body()?.string())

        val playerJs = doc.select("script[src~=(lecteur.js|lecteur_cr.js).*]")

        val oldPlayer = playerJs.attr("src").contains("lecteur.js")
        val mangas = doc.getElementById("mangas")
        val chapitres = doc.getElementById("chapitres")
        val pages = doc.select("#pages > option:not([data-img~=(IMG__|__sy|__Add).*\\.(png|jpe?g)])")
        val image = doc.getElementById("image")

        val mangaName = mangas.dataset()["nom"]
        val chapterName = chapitres.dataset()["nom"]
        val chapterUri = chapitres.dataset()["uri"]

        return pages.filter { it.dataset()["img"]?.isNotEmpty() ?: false }
                .map { page ->
                    val img = page.dataset()["img"]
                    if (oldPlayer) {
                        val src = image.attr("src")
                        val index = src.lastIndexOf("/")
                        val substring = src.substring(0, index + 1)
                        Page(substring + img)
                    } else {
                        val cleanName = mangaName!!.replace("/", "_").replace("?", "")
                        val chapter = chapterName ?: chapterUri
                        val url = "${BuildConfig.JAPSCAN_CDN_BASE_URL}/cr_images/$cleanName/$chapter/$img"
                        Page(url, MosaicPostProcess)
                    }
                }

    }

    object MosaicPostProcess : PostProcess {
        override fun execute(file: File) {
            val source = BitmapFactory.decodeFile(file.absolutePath)
            val newBitmap = Bitmap.createBitmap(source.width, source.height, source.config)
            val comboImage = Canvas(newBitmap)

            val width = source.width.toDouble()
            val height = source.height.toDouble()

            val wp = Math.floor(width / 5)
            val hp = Math.floor(height / 5)
            val x = doubleArrayOf(wp * 2, wp * 4, 0.0, wp * 3, wp)
            val y = doubleArrayOf(hp * 4, hp * 3, hp * 2, hp, 0.0)

            var ph: Double?
            var pw: Double?

            for (i in 1..5) {
                ph = y[i - 1]

                for (j in 1..5) {
                    pw = x[j - 1]
                    val leftDst = (j - 1) * wp
                    val topDst = (i - 1) * hp
                    val rightDst = j * wp
                    val bottomDst = i * hp

                    val leftSrc = pw
                    val topSrc = ph
                    val rightSrc = pw + wp
                    val bottomSrc = ph + hp

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

            source.recycle()
        }
    }
}
