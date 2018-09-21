package com.arnaudpiroelle.manga.ui.tasks

import com.arnaudpiroelle.manga.api.core.rx.plusAssign
import com.arnaudpiroelle.manga.data.TaskRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TasksPresenter(
        private val view: TasksContract.View,
        private val taskRepository: TaskRepository) : TasksContract.UserActionsListener {

    private val subscriptions = CompositeDisposable()

    override fun retrieveTasks() {
        subscriptions += taskRepository.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::displayTasks, {})
    }

    override fun unregister() {
        subscriptions.clear()
    }
}