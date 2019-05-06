package com.arnaudpiroelle.manga.provider.japscanproxy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.arnaudpiroelle.manga.api.model.*
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.File
import java.io.FileOutputStream

class JapScanMangaProvider(private val okHttpClient: OkHttpClient) : MangaProvider {

    private val gson = Gson()
    private val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.JAPSCAN_PROXY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    private val japScanProxyApiService: JapScanProxyApiService = retrofit.create()

    override suspend fun findMangas() = japScanProxyApiService.findMangas().await()
            .map { Manga(it.name, it.alias, "JapScan") }

    override suspend fun findDetails(mangaAlias: String): MangaDetails {
        val details = japScanProxyApiService.findDetails(mangaAlias).await()
        return MangaDetails(details.origin, details.year, details.type, details.kind, details.author, details.summary)
    }

    override suspend fun findChapters(mangaAlias: String) = japScanProxyApiService.findChapters(mangaAlias).await()
            .map { Chapter(it.name, it.manga, it.number) }

    override suspend fun findPages(mangaAlias: String, chapterNumber: String): List<Page> {
        val pagesDesc = japScanProxyApiService.findPages(mangaAlias, chapterNumber).await()
        val postProcess = when (pagesDesc.postProcess) {
            "MOSAIC" -> PostProcessType.MOSAIC
            else -> PostProcessType.NONE
        }
        return pagesDesc.pages.map { Page(BuildConfig.JAPSCAN_PROXY_BASE_URL + it, postProcess) }
    }

    override suspend fun findPage(pageUrl: String) = japScanProxyApiService.findPage(pageUrl).await()

    override suspend fun postProcess(postProcessType: PostProcessType, page: File) {
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
            else -> {
            }
        }
    }
}