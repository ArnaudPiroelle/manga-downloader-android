package com.arnaudpiroelle.manga.core.inject.module

import android.content.Context
import androidx.room.Room
import com.arnaudpiroelle.manga.core.db.AppDatabase
import com.arnaudpiroelle.manga.core.db.dao.HistoryDao
import com.arnaudpiroelle.manga.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.core.db.dao.TaskDao
import toothpick.config.Module


class DatabaseModule(context: Context) : Module() {

    init {
        val appDatabase = providesAppDatabase(context)

        bind(AppDatabase::class.java).toInstance(appDatabase)
        bind(MangaDao::class.java).toInstance(appDatabase.mangaDao())
        bind(HistoryDao::class.java).toInstance(appDatabase.historyDao())
        bind(TaskDao::class.java).toInstance(appDatabase.taskDao())
    }

    private fun providesAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "mangas").build()
    }
}