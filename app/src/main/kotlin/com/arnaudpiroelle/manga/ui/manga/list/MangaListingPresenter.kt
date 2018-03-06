package com.arnaudpiroelle.manga.ui.manga.list

import com.arnaudpiroelle.manga.core.db.MangaDao
import com.arnaudpiroelle.manga.core.rx.plusAssign
import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingContract.View
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MangaListingPresenter(
        private val view: View,
        private val mangaDao: MangaDao) : MangaListingContract.UserActionsListener {

    private val subscriptions = CompositeDisposable()

    override fun register() {
        subscriptions += mangaDao.getAll()
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
        subscriptions += Completable.fromAction { mangaDao.delete(manga) }
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})
    }
}
