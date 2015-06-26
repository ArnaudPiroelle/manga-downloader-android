package com.arnaudpiroelle.manga.ui.manga.list;

import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

        Observable.create(new Observable.OnSubscribe<List<Manga>>() {
            @Override
            public void call(Subscriber<? super List<Manga>> subscriber) {
                try (CursorList<Manga> mangaCursorList = Query.all(Manga.class).get()) {
                    subscriber.onNext(mangaCursorList.asList());
                }

                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Manga>>() {
                    @Override
                    public void call(List<Manga> mangas) {
                        callback.onListingLoaded(mangas);
                    }
                });
    }

    public interface MangaListingCallback {
        void onListingLoading();

        void onListingLoaded(List<Manga> mangas);
    }
}
