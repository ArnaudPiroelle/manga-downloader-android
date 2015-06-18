package com.arnaudpiroelle.manga.ui.manga.list;

import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;

public class MangaListingPresenter implements Presenter<Manga> {

    private MangaListingCallback callback;

    public MangaListingPresenter(MangaListingCallback callback) {
        this.callback = callback;
    }

    @Override
    public void list() {
        callback.onListingLoading();

        Observable.<List<Manga>>create(subscriber -> {

            try (CursorList<Manga> mangaCursorList = Query.all(Manga.class).get()) {
                subscriber.onNext(mangaCursorList.asList());
            }

            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onListingLoaded);
    }

    @Override
    public void filter(String query) {

    }

    public interface MangaListingCallback {
        void onListingLoading();
        void onListingLoaded(List<Manga> mangas);
    }
}
