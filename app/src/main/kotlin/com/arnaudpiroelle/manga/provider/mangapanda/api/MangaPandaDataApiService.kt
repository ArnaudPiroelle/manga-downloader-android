package com.arnaudpiroelle.manga.provider.mangapanda.api

import retrofit.client.Response
import retrofit.http.GET
import retrofit.http.Path

interface MangaPandaDataApiService {
    @GET("/{mangaAlias}/{chapterNumber}/{pageName}.{extension}")
    fun downloadPage(@Path("mangaAlias") mangaAlias: String, @Path("chapterNumber") chapterNumber: String,
                     @Path("pageName") pageName: String, @Path("extension") extension: String): Response
}
