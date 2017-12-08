package com.arnaudpiroelle.manga.provider.japscan.api

import retrofit.client.Response
import retrofit.http.GET
import retrofit.http.Path

interface JapScanApiService {
    @GET("/mangas/")
    fun getMangaList(): Response

    @GET("/mangas/{mangaAlias}/")
    fun getManga(@Path("mangaAlias") mangaAlias: String): Response

    @GET("/lecture-en-ligne/{mangaAlias}/{chapterNumber}")
    fun getReader(@Path("mangaAlias") mangaAlias: String, @Path("chapterNumber") chapterNumber: String): Response

    @GET("/lecture-en-ligne/{mangaAlias}/{chapterNumber}/{pageNumber}.html")
    fun getPage(@Path("mangaAlias") mangaAlias: String, @Path("chapterNumber") chapterNumber: String, @Path("pageNumber") pageNumber: String): Response
}
