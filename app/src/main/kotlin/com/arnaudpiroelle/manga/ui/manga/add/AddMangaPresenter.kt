package com.arnaudpiroelle.manga.ui.manga.add

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.core.rx.plusAssign
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.core.db.dao.TaskDao
import com.arnaudpiroelle.manga.data.model.Task
import io.reactivex.Completable.fromAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddMangaPresenter(
        private val view: AddMangaContract.View,
        private val providerRegistry: ProviderRegistry,
        private val taskDao: TaskDao,
        private val mangaDao: MangaDao) : AddMangaContract.UserActionsListener {

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
        subscriptions += fromAction {
            val id = mangaDao.insert(com.arnaudpiroelle.manga.data.model.Manga(name = manga.name, alias = manga.alias, provider = manga.provider))
            taskDao.insert(Task(type = Task.Type.RETRIEVE_CHAPTERS, label = manga.name, item = id))
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.closeWizard()
                }, {

                })

    }
}