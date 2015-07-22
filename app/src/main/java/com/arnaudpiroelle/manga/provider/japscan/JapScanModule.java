package com.arnaudpiroelle.manga.provider.japscan;

import com.arnaudpiroelle.manga.BuildConfig;
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanApiService;
import com.arnaudpiroelle.manga.provider.japscan.api.JapScanDataApiService;
import com.arnaudpiroelle.manga.provider.japscan.downloader.JapScanDownloader;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import static com.arnaudpiroelle.manga.BuildConfig.JAPSCAN_BASE_URL;
import static com.arnaudpiroelle.manga.BuildConfig.JAPSCAN_CDN_BASE_URL;
import static retrofit.RestAdapter.LogLevel.BASIC;
import static retrofit.RestAdapter.LogLevel.NONE;

@Module
public class JapScanModule {

    @Provides
    @Singleton
    @Named("JapScanRestAdapter")
    public RestAdapter provideJapScanRestAdapter(OkClient client) {
        return new RestAdapter.Builder()
                .setEndpoint(JAPSCAN_BASE_URL)
                .setLogLevel(NONE)
                .setClient(client)
                .build();
    }

    @Provides
    @Singleton
    @Named("JapScanCdnRestAdapter")
    public RestAdapter provideJapScanCdnRestAdapter(OkClient client) {
        return new RestAdapter.Builder()
                .setEndpoint(JAPSCAN_CDN_BASE_URL)
                .setLogLevel(NONE)
                .setClient(client)
                .build();
    }

    @Provides
    @Singleton
    public JapScanDataApiService provideJapScanDataApiService(@Named("JapScanCdnRestAdapter") RestAdapter restAdapter) {
        return restAdapter.create(JapScanDataApiService.class);
    }

    @Provides
    @Singleton
    public JapScanApiService provideJapScanApiService(@Named("JapScanRestAdapter") RestAdapter restAdapter) {
        return restAdapter.create(JapScanApiService.class);
    }

    @Provides
    @Singleton
    public JapScanDownloader provideJapScanDownloader(JapScanApiService japScanApiService, JapScanDataApiService japScanDataApiService) {
        return new JapScanDownloader(japScanApiService, japScanDataApiService);
    }
}
