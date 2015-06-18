package com.arnaudpiroelle.manga.ui.manga.add.provider;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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

        Observable.<List<MangaProvider>>create(subscriber -> {

            subscriber.onNext(providerRegistry.list());

            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onListingLoaded);
    }

    @Override
    public void filter(String query) {

    }

    public interface ProviderListingCallback {
        void onListingLoading();
        void onListingLoaded(List<MangaProvider> providers);
    }
}
