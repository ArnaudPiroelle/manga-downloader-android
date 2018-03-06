package com.arnaudpiroelle.manga.provider.mangapanda.api

import retrofit.client.Response
import retrofit.http.GET
import retrofit.http.Path

interface MangaPandaApiService {
    @GET("/alphabetical")
    fun getMangaList(): Response

    @GET("/{mangaAlias}")
    fun getManga(@Path("mangaAlias") mangaAlias: String): Response

    @GET("/{mangaAlias}/{chapterNumber}")
    fun getReader(@Path("mangaAlias") mangaAlias: String, @Path("chapterNumber") chapterNumber: String): Response

    @GET("/{mangaAlias}/{chapterNumber}/{pageNumber}")
    fun getPage(@Path("mangaAlias") mangaAlias: String, @Path("chapterNumber") chapterNumber: String, @Path("pageNumber") pageNumber: String): Response
}
