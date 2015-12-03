package com.arnaudpiroelle.manga.provider.japscan.api

import retrofit.client.Response
import retrofit.http.GET
import retrofit.http.Path

interface JapScanDataApiService {
    @GET("/cr-images/{mangaAlias}/{chapterNumber}/{pageNumber}.{extension}")
    fun downloadPage(@Path("mangaAlias") mangaAlias: String, @Path("chapterNumber") chapterNumber: String,
                     @Path("pageNumber") pageNumber: String, @Path("extension") extension: String): Response
}
