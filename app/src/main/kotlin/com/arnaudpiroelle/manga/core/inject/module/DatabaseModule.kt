package com.arnaudpiroelle.manga.core.inject.module

import android.arch.persistence.room.Room
import android.content.Context
import com.arnaudpiroelle.manga.core.db.AppDatabase
import com.arnaudpiroelle.manga.core.db.HistoryDao
import com.arnaudpiroelle.manga.core.db.MangaDao
import toothpick.config.Module


class DatabaseModule(context: Context) : Module() {

    init {
        val appDatabase = providesAppDatabase(context)

        bind(AppDatabase::class.java).toInstance(appDatabase)
        bind(MangaDao::class.java).toInstance(appDatabase.mangaDao())
        bind(HistoryDao::class.java).toInstance(appDatabase.historyDao())
    }

    private fun providesAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "mangas").build()
    }
}