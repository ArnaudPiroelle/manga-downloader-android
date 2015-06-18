package com.arnaudpiroelle.manga.core.inject.module;

import android.content.Context;

import com.arnaudpiroelle.manga.MangaApplication;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
import com.arnaudpiroelle.manga.provider.japscan.downloader.JapScanDownloader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import retrofit.client.OkClient;

import static com.arnaudpiroelle.manga.core.provider.ProviderRegistry.ProviderRegistryBuilder.createProviderRegister;

@Module
public class ApplicationModule {
    private final Context mContext;

    public ApplicationModule(Context context) {
        mContext = context;
    }

    @Provides
    @Singleton
    public Context providesContext(){
        return mContext;
    }

    @Provides
    @Singleton
    public OkClient providesHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(60, TimeUnit.SECONDS);
        client.setConnectTimeout(60, TimeUnit.SECONDS);

        return new OkClient(client);
    }

    @Provides
    @Singleton
    public ProviderRegistry providesProviderRegistry(JapScanDownloader japScanDownloader) {
        return createProviderRegister()
                .withProvider(japScanDownloader)
                .build();
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Provides
    @Singleton
    public EventBus providesEventBus(){
        return EventBus.getDefault();
    }

}
