package com.arnaudpiroelle.manga.provider.mangapanda.api;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MangaPandaApiService {
    @GET("/alphabetical")
    Response getMangaList();

    @GET("/{mangaAlias}")
    Response getManga(@Path("mangaAlias") String mangaAlias);

    @GET("/{mangaAlias}/{chapterNumber}")
    Response getReader(@Path("mangaAlias") String mangaAlias, @Path("chapterNumber") String chapterNumber);

    @GET("/{mangaAlias}/{chapterNumber}/{pageNumber}")
    Response getPage(@Path("mangaAlias") String mangaAlias, @Path("chapterNumber") String chapterNumber, @Path("pageNumber") String pageNumber);
}
