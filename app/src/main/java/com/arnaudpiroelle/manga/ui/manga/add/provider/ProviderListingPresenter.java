package com.arnaudpiroelle.manga.ui.manga.add.provider;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ProviderListingPresenter implements Presenter<MangaProvider> {

    private ProviderRegistry providerRegistry;
    private ProviderListingCallback callback;

    public ProviderListingPresenter(ProviderListingCallback callback, ProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
        this.callback = callback;
    }

    @Override
    public void list() {
        callback.onListingLoading();

        Observable.create(new Observable.OnSubscribe<List<MangaProvider>>() {
            @Override
            public void call(Subscriber<? super List<MangaProvider>> subscriber) {
                subscriber.onNext(providerRegistry.list());

                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MangaProvider>>() {
                    @Override
                    public void call(List<MangaProvider> mangaProviders) {
                        callback.onListingLoaded(mangaProviders);
                    }
                });
    }

    public interface ProviderListingCallback {
        void onListingLoading();

        void onListingLoaded(List<MangaProvider> providers);
    }
}
