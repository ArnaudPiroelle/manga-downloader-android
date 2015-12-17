package com.arnaudpiroelle.manga.ui.history

import com.arnaudpiroelle.manga.core.ui.presenter.Presenter
import com.arnaudpiroelle.manga.model.History
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import se.emilsjolander.sprinkles.Query
import java.util.*

class HistoryPresenter(internal var callback:

                       HistoryPresenter.HistoryListingCallback) : Presenter<History> {

    override fun list() {
        callback.onListingLoading()

        Observable.create(Observable.OnSubscribe<List<History>> { subscriber ->
            Query.all(History::class.java).get().use {
                val histories = it.asList()

                Collections.reverse(histories)
                subscriber.onNext(histories)
            }

            subscriber.onCompleted()
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { callback.onListingLoaded(it) }
    }

    fun cleanHistory() {
        Observable.from(Query.all(History::class.java).get().asList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { it.delete() },
                        { },
                        { this.list() })
    }

    interface HistoryListingCallback {
        fun onListingLoading()

        fun onListingLoaded(histories: List<History>)
    }
}
