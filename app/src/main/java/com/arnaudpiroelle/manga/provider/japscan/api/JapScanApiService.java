package com.arnaudpiroelle.manga.provider.japscan.api;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public interface JapScanApiService {
    @GET("/mangas/")
    Response getMangaList();

    @GET("/mangas/{mangaAlias}/")
    Response getManga(@Path("mangaAlias") String mangaAlias);

    @GET("/lecture-en-ligne/{mangaAlias}/{chapterNumber}")
    Response getReader(@Path("mangaAlias") String mangaAlias, @Path("chapterNumber") String chapterNumber);

    @GET("/lecture-en-ligne/{mangaAlias}/{chapterNumber}/{pageNumber}.html")
    Response getPage(@Path("mangaAlias") String mangaAlias, @Path("chapterNumber") String chapterNumber, @Path("pageNumber") String pageNumber);
}
