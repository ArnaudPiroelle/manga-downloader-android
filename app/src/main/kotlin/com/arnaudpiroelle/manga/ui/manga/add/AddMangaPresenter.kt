package com.arnaudpiroelle.manga.ui.manga.add

import com.arnaudpiroelle.manga.api.core.rx.plusAssign
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddMangaPresenter(
        private val view: AddMangaContract.View,
        private val providerRegistry: ProviderRegistry) : AddMangaContract.UserActionsListener {

    private val subscriptions = CompositeDisposable()

    override fun fillProviders() {
        view.displayProviders(providerRegistry.list())
    }

    override fun findMangas(provider: MangaProvider) {
        subscriptions += provider.findMangas()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::displayMangas, {})

    }

    override fun filterBy(name: String) {
        view.filterMangasBy(name)
    }

    override fun selectManga(manga: Manga) {
        view.displayChapters(manga)
    }
}