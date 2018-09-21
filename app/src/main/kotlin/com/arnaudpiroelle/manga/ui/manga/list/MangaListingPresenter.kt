package com.arnaudpiroelle.manga.ui.manga.list

import com.arnaudpiroelle.manga.api.core.rx.plusAssign
import com.arnaudpiroelle.manga.data.MangaRepository
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingContract.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MangaListingPresenter(
        private val view: View,
        private val mangaRepository: MangaRepository) : MangaListingContract.UserActionsListener {

    private val subscriptions = CompositeDisposable()

    override fun register() {
        subscriptions += mangaRepository.getAll()
                .subscribeOn(Schedulers.io())
                .map { it.sortedBy { it.name } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::displayMangas, {})
    }

    override fun unregister() {
        subscriptions.clear()
    }

    override fun addManga() {
        view.openNewMangaWizard()
    }

    override fun askForRemove(manga: Manga) {
        view.displayRemoveConfirmation(manga)
    }

    override fun askForModify(manga: Manga) {
        view.displayModificationDialog(manga)
    }

    override fun remove(manga: Manga) {
        subscriptions += mangaRepository.delete(manga)
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})
    }
}
