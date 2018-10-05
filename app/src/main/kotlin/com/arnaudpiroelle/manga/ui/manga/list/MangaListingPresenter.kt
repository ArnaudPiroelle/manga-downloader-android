package com.arnaudpiroelle.manga.ui.manga.list

import com.arnaudpiroelle.manga.api.core.rx.plusAssign
import com.arnaudpiroelle.manga.data.ChapterRepository
import com.arnaudpiroelle.manga.data.MangaRepository
import com.arnaudpiroelle.manga.data.PageRepository
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.data.model.MangaWithCover
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingContract.View
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MangaListingPresenter(
        private val view: View,
        private val mangaRepository: MangaRepository,
        private val chapterRepository: ChapterRepository,
        private val pageRepository: PageRepository) : MangaListingContract.UserActionsListener {

    private val subscriptions = CompositeDisposable()

    override fun register() {
        subscriptions += mangaRepository.getAll()
                .flatMapSingle { items ->
                    Flowable.fromIterable(items)
                            .flatMapMaybe { manga ->
                                chapterRepository.getFirstChapterFor(manga.id)
                                        .flatMap { pageRepository.getPagesFor(it.id) }
                                        .map { it.firstOrNull()?.url }
                                        .defaultIfEmpty("")
                                        .map { MangaWithCover(manga.id, manga.name, it) }
                            }
                            .toList()
                }.subscribeOn(Schedulers.io())
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
