package com.arnaudpiroelle.manga.ui.manga.list

import com.arnaudpiroelle.manga.core.ui.presenter.Presenter
import com.arnaudpiroelle.manga.model.Manga
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import se.emilsjolander.sprinkles.Query

class MangaListingPresenter(private val callback: MangaListingPresenter.MangaListingCallback) : Presenter<Manga> {

    override fun list() {
        callback.onListingLoading()

        Observable.create<List<Manga>> {
            subscriber ->
            Query.all(Manga::class.java).get()
                    .use { mangaCursorList -> subscriber.onNext(mangaCursorList.asList().sortedBy { it.name }) }

            subscriber.onCompleted()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ callback.onListingLoaded(it) })
    }

    interface MangaListingCallback {
        fun onListingLoading()

        fun onListingLoaded(mangas: List<Manga>)
    }
}
