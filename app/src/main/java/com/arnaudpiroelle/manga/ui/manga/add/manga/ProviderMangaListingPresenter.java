package com.arnaudpiroelle.manga.ui.manga.add.manga;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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

        Observable.<List<Manga>>create(subscriber -> {
            subscriber.onNext(provider.findMangas());
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onListingLoaded);
    }

    private void onListingLoaded(List<Manga> mangas){
        this.mangas = mangas;
        callback.onListingLoaded(mangas);
    }

    @Override
    public void filter(String query) {
        Observable.from(mangas)
                .filter(manga -> manga.getName().toLowerCase().contains(query.toLowerCase()))
                .toSortedList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onListingFiltered);
    }

    public interface ProviderMangaListingPresenterCallback {
        void onListingLoading();

        void onListingLoaded(List<Manga> mangas);

        void onListingFiltered(List<Manga> mangas);
    }
}
