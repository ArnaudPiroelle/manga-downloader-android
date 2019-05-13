package com.arnaudpiroelle.manga.provider.japscanproxy

import kotlinx.coroutines.Deferred
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface JapScanProxyApiService {

    @GET("/mangas/")
    fun findMangas(): Deferred<List<JapScanManga>>

    @GET("/mangas/{mangaAlias}")
    fun findDetails(@Path("mangaAlias") mangaAlias: String): Deferred<JapScanDetails>

    @GET("/mangas/{mangaAlias}/chapters")
    fun findChapters(@Path("mangaAlias") mangaAlias: String): Deferred<List<JapScanChapter>>

    @GET("/mangas/{mangaAlias}/chapters/{chapterNumber}")
    fun findPages(@Path("mangaAlias") mangaAlias: String, @Path("chapterNumber") chapterNumber: String): Deferred<JapScanPages>

    @GET
    fun findPage(@Url url: String): Deferred<ResponseBody>

    data class JapScanManga(val name: String, val alias: String, val thumbnail: String)
    data class JapScanChapter(val name: String, val manga: String, val number: String)
    data class JapScanPages(val postProcess: String, val pages: List<String>)
    data class JapScanDetails(
            val origin: String = "",
            val year: String = "",
            val type: String = "",
            val kind: String = "",
            val author: String = "",
            val summary: String = ""
    )
}
