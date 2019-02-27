package com.arnaudpiroelle.manga.provider.japscanproxy

import com.arnaudpiroelle.manga.api.model.Chapter
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.api.model.Page
import com.arnaudpiroelle.manga.api.model.PostProcessType
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File

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

    override fun findChapters(mangaAlias: String): List<Chapter> {
        val request = Request.Builder()
                .url("${BuildConfig.JAPSCAN_PROXY_BASE_URL}/mangas/$mangaAlias")
                .build()

        val response = okHttpClient.newCall(request).execute()
        val fooType = object : TypeToken<List<JapScanChapter>>() {}
        val chapters: List<JapScanChapter> = gson.fromJson(response.body()?.string(), fooType.type)
        return chapters.map { Chapter(it.name, it.manga, it.number) }
    }

    override fun findPages(mangaAlias: String, chapterNumber: String): List<Page> {
        val request = Request.Builder()
                .url("${BuildConfig.JAPSCAN_PROXY_BASE_URL}/mangas/$mangaAlias/$chapterNumber")
                .build()

        val response = okHttpClient.newCall(request).execute()
        val fooType = object : TypeToken<List<String>>() {}
        val pages: List<String> = gson.fromJson(response.body()?.string(), fooType.type)
        return pages.map { Page(BuildConfig.JAPSCAN_PROXY_BASE_URL + it) }
    }

    override fun findPage(pageUrl: String): Response {
        val request = Request.Builder()
                .url(pageUrl)
                .build()

        return okHttpClient.newCall(request).execute()
    }

    override fun postProcess(postProcessType: PostProcessType, page: File) {
        // DO nothing
    }

    data class JapScanManga(val name: String, val alias: String, val thumbnail: String)
    data class JapScanChapter(val name: String, val manga: String, val number: String)
}