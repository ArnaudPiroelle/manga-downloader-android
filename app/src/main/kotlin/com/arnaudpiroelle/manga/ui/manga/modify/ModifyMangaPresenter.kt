package com.arnaudpiroelle.manga.ui.manga.modify

import com.arnaudpiroelle.manga.api.model.Chapter
import com.arnaudpiroelle.manga.core.db.MangaDao
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.rx.plusAssign
import io.reactivex.Completable.fromAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ModifyMangaPresenter(
        private val view: ModifyMangaContract.View,
        private val providerRegistry: ProviderRegistry,
        private val mangaDao: MangaDao) : ModifyMangaContract.UserActionsListener {

    private val subscriptions = CompositeDisposable()

    override fun findChapters(provider: String, mangaAlias: String, lastChapter: String) {
        val mangaProvider = providerRegistry.find(provider)

        subscriptions += mangaProvider.findChapters(mangaAlias)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ chapters ->
                    val allChapter = Chapter(chapterNumber = "all")

                    val allChapters: List<Chapter> = ArrayList<Chapter>().apply {
                        add(allChapter)
                        addAll(chapters)
                    }

                    val selection = allChapters.indexOfFirst { it.chapterNumber == lastChapter }
                    view.displayChapters(allChapters, selection)
                }, {

                })
    }

    override fun selectChapter(provider: String, name: String, mangaAlias: String, chapterNumber: String) {
        subscriptions += mangaDao.getByAlias(mangaAlias)
                .defaultIfEmpty(com.arnaudpiroelle.manga.model.db.Manga(name, mangaAlias, provider))
                .flatMapCompletable {
                    it.lastChapter = chapterNumber
                    fromAction {
                        mangaDao.insertAll(it)
                    }
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                        view::closeWizard,
                        { throwable -> throwable.printStackTrace() }
                )
    }

}
