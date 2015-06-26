package com.arnaudpiroelle.manga.ui.manga.add.manga;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ProviderMangaListingPresenter implements Presenter<Manga> {

    private final ProviderMangaListingPresenterCallback callback;
    private final MangaProvider provider;
    private List<Manga> mangas = new ArrayList<>();

    public ProviderMangaListingPresenter(ProviderMangaListingPresenterCallback callback, MangaProvider provider) {
        this.callback = callback;
        this.provider = provider;
    }

    @Override
    public void list() {
        callback.onListingLoading();

        Observable.create(new Observable.OnSubscribe<List<Manga>>() {
            @Override
            public void call(Subscriber<? super List<Manga>> subscriber) {
                subscriber.onNext(provider.findMangas());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Manga>>() {
                    @Override
                    public void call(List<Manga> mangas) {
                        onListingLoaded(mangas);
                    }
                });
    }

    private void onListingLoaded(List<Manga> mangas) {
        this.mangas = mangas;
        callback.onListingLoaded(mangas);
    }

    public void filter(final String query) {
        Observable.from(mangas)
                .filter(new Func1<Manga, Boolean>() {
                    @Override
                    public Boolean call(Manga manga) {
                        return manga.getName().toLowerCase().contains(query.toLowerCase());
                    }
                })
                .toSortedList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Manga>>() {
                    @Override
                    public void call(List<Manga> mangas) {
                        callback.onListingFiltered(mangas);
                    }
                });
    }

    public interface ProviderMangaListingPresenterCallback {
        void onListingLoading();

        void onListingLoaded(List<Manga> mangas);

        void onListingFiltered(List<Manga> mangas);
    }
}
