package com.arnaudpiroelle.manga.api.provider

import com.arnaudpiroelle.manga.api.model.*
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.File

interface MangaProvider {
    suspend fun findMangas(): List<Manga>

    suspend fun findDetails(mangaAlias: String): MangaDetails

    suspend fun findChapters(mangaAlias: String): List<Chapter>

    suspend fun findPages(mangaAlias: String, chapterNumber: String): List<Page>

    suspend fun findPage(pageUrl: String): ResponseBody

    suspend fun postProcess(postProcessType: PostProcessType, page: File)
}
