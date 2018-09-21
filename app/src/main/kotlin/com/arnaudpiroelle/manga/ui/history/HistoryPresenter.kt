package com.arnaudpiroelle.manga.ui.history

import com.arnaudpiroelle.manga.api.core.rx.plusAssign
import com.arnaudpiroelle.manga.core.db.dao.HistoryDao
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HistoryPresenter(
        private val view: HistoryContract.View,
        private val historyDao: HistoryDao) : HistoryContract.UserActionsListener {

    private val disposable = CompositeDisposable()

    override fun register() {
        disposable += historyDao.getAll()
                .subscribeOn(Schedulers.io())
                .map { it.reversed() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::displayHistories, {})
    }

    override fun unregister() {
        disposable.clear()
    }

    override fun cleanHistory() {
        disposable += Completable.fromAction { historyDao.deleteAll() }
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})
    }
}
