package com.arnaudpiroelle.manga.provider.mock

import com.arnaudpiroelle.manga.api.model.Chapter
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.api.model.Page
import com.arnaudpiroelle.manga.api.model.PostProcessType
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File

class MockMangaProvider(private val okHttpClient: OkHttpClient) : MangaProvider {
    override fun findMangas() = listOf(Manga("One Piece", "one-piece", "Mock"))

    override fun findChapters(mangaAlias: String) = listOf(Chapter("One Piece - 900", "one-piece", "900"))

    override fun findPages(mangaAlias: String, chapterNumber: String) = listOf<Page>()
    override fun findPage(pageUrl: String): Response {
        return okHttpClient.newCall(Request.Builder().url(pageUrl).build()).execute()
    }

    override fun postProcess(postProcessType: PostProcessType, page: File) {

    }
}