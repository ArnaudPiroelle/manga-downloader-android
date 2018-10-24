package com.arnaudpiroelle.manga.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arnaudpiroelle.manga.data.core.db.dao.TaskDao

class TasksViewModel(private val taskDao: TaskDao) : ViewModel() {
    val tasks = LivePagedListBuilder(taskDao.observeAll(), PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(8)
            .build())
            .build()
}