package com.arnaudpiroelle.manga.ui.history;

import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.model.History;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;

public class HistoryPresenter implements Presenter<History> {

    HistoryListingCallback callback;

    public HistoryPresenter(HistoryListingCallback callback) {
        this.callback = callback;
    }

    @Override
    public void list() {
        callback.onListingLoading();

        Observable.<List<History>>create((subscriber) -> {
            try (CursorList<History> historyCursorList = Query.all(History.class).get()) {
                subscriber.onNext(historyCursorList.asList());
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

    public interface HistoryListingCallback {
        void onListingLoading();

        void onListingLoaded(List<History> histories);
    }
}
