package com.arnaudpiroelle.manga.provider.japscanproxy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.arnaudpiroelle.manga.api.model.*
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream

class JapScanMangaProvider(val okHttpClient: OkHttpClient) : MangaProvider {

    private val gson = Gson()

    override fun findMangas(): List<Manga> {
        val request = Request.Builder()
                .url("${BuildConfig.JAPSCAN_PROXY_BASE_URL}/mangas/")
                .build()

        val response = okHttpClient.newCall(request).execute()
        val fooType = object : TypeToken<List<JapScanManga>>() {}
        val mangas: List<JapScanManga> = gson.fromJson(response.body()?.string(), fooType.type)

        return mangas.map { Manga(it.name, it.alias, "JapScan") }

    }

    override fun findDetails(mangaAlias: String): MangaDetails {
        val request = Request.Builder()
                .url("${BuildConfig.JAPSCAN_PROXY_BASE_URL}/mangas/$mangaAlias")
                .build()

        val response = okHttpClient.newCall(request).execute()
        val string = response.body()?.string()

        println(string)
        return gson.fromJson(string, MangaDetails::class.java)
    }

    override fun findChapters(mangaAlias: String): List<Chapter> {
        val request = Request.Builder()
                .url("${BuildConfig.JAPSCAN_PROXY_BASE_URL}/mangas/$mangaAlias/chapters")
                .build()

        val response = okHttpClient.newCall(request).execute()
        val fooType = object : TypeToken<List<JapScanChapter>>() {}
        val chapters: List<JapScanChapter> = gson.fromJson(response.body()?.string(), fooType.type)
        return chapters.map { Chapter(it.name, it.manga, it.number) }
    }

    override fun findPages(mangaAlias: String, chapterNumber: String): List<Page> {
        val request = Request.Builder()
                .url("${BuildConfig.JAPSCAN_PROXY_BASE_URL}/mangas/$mangaAlias/chapters/$chapterNumber")
                .build()

        val response = okHttpClient.newCall(request).execute()
        val pagesDesc: JapScanPages = gson.fromJson(response.body()?.string(), JapScanPages::class.java)
        val postProcess = when (pagesDesc.postProcess) {
            "MOSAIC" -> PostProcessType.MOSAIC
            else -> PostProcessType.NONE
        }
        return pagesDesc.pages.map { Page(BuildConfig.JAPSCAN_PROXY_BASE_URL + it, postProcess) }
    }

    override fun findPage(pageUrl: String): Response {
        val request = Request.Builder()
                .url(pageUrl)
                .build()

        return okHttpClient.newCall(request).execute()
    }

    override fun postProcess(postProcessType: PostProcessType, page: File) {
        when (postProcessType) {
            PostProcessType.MOSAIC -> {
                val source = BitmapFactory.decodeFile(page.absolutePath)
                val newBitmap = Bitmap.createBitmap(source.width, source.height, source.config)
                val comboImage = Canvas(newBitmap)
                val newBitmap2 = Bitmap.createBitmap(source.width, source.height, source.config)
                val comboImage2 = Canvas(newBitmap2)


                val columnWidth = 100
                val lineHeight = 100
                val width = source.width.toDouble()
                val height = source.height.toDouble()

                var numColumn = 0
                while (width > numColumn * 2 * columnWidth + 2 * columnWidth) {
                    numColumn++
                }

                var numLine = 0
                while (height > numLine * 2 * lineHeight + 2 * lineHeight) {
                    numLine++
                }

                for (i in 0 until numColumn) {
                    val column1 = i * 2
                    val column2 = column1 + 1

                    var src = Rect(column1 * columnWidth, 0, column2 * columnWidth, height.toInt())
                    var dst = Rect(column2 * columnWidth, 0, (column2 + 1) * columnWidth, height.toInt())
                    comboImage.drawBitmap(source, src, dst, null)

                    src = Rect(column2 * columnWidth, 0, (column2 + 1) * columnWidth, height.toInt())
                    dst = Rect(column1 * columnWidth, 0, column2 * columnWidth, height.toInt())
                    comboImage.drawBitmap(source, src, dst, null)
                }

                var src = Rect(numColumn * 2 * columnWidth, 0, width.toInt(), height.toInt())
                var dst = Rect(numColumn * 2 * columnWidth, 0, width.toInt(), height.toInt())
                comboImage.drawBitmap(source, src, dst, null)

                source.recycle()

                for (i in 0 until numLine) {
                    val line1 = i * 2
                    val line2 = line1 + 1

                    src = Rect(0, line1 * lineHeight, width.toInt(), line2 * lineHeight)
                    dst = Rect(0, line2 * lineHeight, width.toInt(), (line2 + 1) * lineHeight)
                    comboImage2.drawBitmap(newBitmap, src, dst, null)

                    src = Rect(0, line2 * lineHeight, width.toInt(), (line2 + 1) * lineHeight)
                    dst = Rect(0, line1 * lineHeight, width.toInt(), line2 * lineHeight)
                    comboImage2.drawBitmap(newBitmap, src, dst, null)
                }

                src = Rect(0, numLine * 2 * lineHeight, width.toInt(), height.toInt())
                dst = Rect(0, numLine * 2 * lineHeight, width.toInt(), height.toInt())
                comboImage2.drawBitmap(newBitmap, src, dst, null)

                newBitmap.recycle()

                FileOutputStream(page).use { os ->
                    newBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.flush()
                    os.close()
                }

                newBitmap2.recycle()
            }
        }
    }

    data class JapScanManga(val name: String, val alias: String, val thumbnail: String)
    data class JapScanChapter(val name: String, val manga: String, val number: String)
    data class JapScanPages(val postProcess: String, val pages: List<String>)
}