package com.arnaudpiroelle.manga.ui.manga.modify

import com.arnaudpiroelle.manga.core.db.MangaDao
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.rx.plusAssign
import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.model.network.Chapter
import io.reactivex.Completable.fromAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ModifyMangaPresenter(
        private val view: ModifyMangaContract.View,
        private val providerRegistry: ProviderRegistry,
        private val mangaDao: MangaDao) : ModifyMangaContract.UserActionsListener {

    private val subscriptions = CompositeDisposable()

    override fun findChapters(manga: Manga) {
        val provider = providerRegistry.find(manga.provider!!)

        subscriptions += provider.findChapters(manga)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ chapters ->
                    val allChapter = Chapter(chapterNumber = "all")

                    val allChapters: List<Chapter> = ArrayList<Chapter>().apply {
                        add(allChapter)
                        addAll(chapters)
                    }

                    val selection = allChapters.indexOf(allChapters.find { it.chapterNumber.equals(manga.lastChapter) })
                    view.displayChapters(allChapters, selection)
                }, {

                })


        /*
        val provider = providerRegistry.find(manga!!.provider!!)
        provider?.apply {
            findChapters(manga!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { chapters ->

                        val allChapter = Chapter()
                        allChapter.chapterNumber = "all"

                        val allChapters: List<Chapter> = ArrayList<Chapter>().apply {
                            add(allChapter)
                            addAll(chapters)
                        }

                        mAdapter.datas = allChapters
                        mAdapter.notifyDataSetChanged()

                        val indexOfRaw = allChapters.indexOf(allChapters.find { it.chapterNumber.equals(manga!!.lastChapter) })
                        dialog.listView.setSelection(indexOfRaw)
                        dialog.listView.setItemChecked(indexOfRaw, true)
                    }
        }
        */
    }

    override fun selectChapter(manga: Manga, chapter: Chapter) {
        manga.lastChapter = chapter.chapterNumber

        subscriptions += fromAction { mangaDao.insertAll(manga) }
                .subscribeOn(Schedulers.io())
                .subscribe(view::closeWizard, {})


    }

}
