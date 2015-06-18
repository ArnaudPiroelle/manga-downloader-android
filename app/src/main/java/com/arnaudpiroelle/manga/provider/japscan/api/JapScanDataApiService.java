package com.arnaudpiroelle.manga.provider.japscan.api;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public interface JapScanDataApiService {
    @GET("/lecture-en-ligne/{mangaAlias}/{chapterNumber}/{pageNumber}.{extension}")
    Response downloadPage(@Path("mangaAlias") String mangaAlias, @Path("chapterNumber") String chapterNumber,
                          @Path("pageNumber") String pageNumber, @Path("extension") String extension);
}
