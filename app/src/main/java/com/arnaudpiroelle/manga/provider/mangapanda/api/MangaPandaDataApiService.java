package com.arnaudpiroelle.manga.provider.mangapanda.api;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public interface MangaPandaDataApiService {
    @GET("/{mangaAlias}/{chapterNumber}/{pageName}.{extension}")
    Response downloadPage(@Path("mangaAlias") String mangaAlias, @Path("chapterNumber") String chapterNumber,
                          @Path("pageName") String pageName, @Path("extension") String extension);
}
