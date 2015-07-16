package com.arnaudpiroelle.manga.provider.mangapanda;

import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaApiService;
import com.arnaudpiroelle.manga.provider.mangapanda.api.MangaPandaDataApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import static com.arnaudpiroelle.manga.BuildConfig.MANGAPANDA_BASE_URL;
import static com.arnaudpiroelle.manga.BuildConfig.MANGAPANDA_CDN_BASE_URL;
import static retrofit.RestAdapter.LogLevel.NONE;

@Module
public class MangaPandaModule {

    @Provides
    @Singleton
    public MangaPandaApiService providesMangaPandaApiService(OkClient client) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MANGAPANDA_BASE_URL)
                .setLogLevel(NONE)
                .setClient(client)
                .build();

        return restAdapter.create(MangaPandaApiService.class);
    }

    @Provides
    @Singleton
    public MangaPandaDataApiService providesMangaPandaDataApiService(OkClient client) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MANGAPANDA_CDN_BASE_URL)
                .setLogLevel(NONE)
                .setClient(client)
                .build();

        return restAdapter.create(MangaPandaDataApiService.class);
    }
}
