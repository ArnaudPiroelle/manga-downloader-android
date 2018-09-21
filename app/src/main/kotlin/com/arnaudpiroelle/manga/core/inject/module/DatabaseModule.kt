package com.arnaudpiroelle.manga.core.inject.module

import android.content.Context
import androidx.room.Room
import com.arnaudpiroelle.manga.data.core.db.AppDatabase
import com.arnaudpiroelle.manga.data.core.db.dao.*
import toothpick.config.Module


class DatabaseModule(context: Context) : Module() {

    init {
        val appDatabase = providesAppDatabase(context)

        bind(AppDatabase::class.java).toInstance(appDatabase)
        bind(MangaDao::class.java).toInstance(appDatabase.mangaDao())
        bind(ChapterDao::class.java).toInstance(appDatabase.chapterDao())
        bind(PageDao::class.java).toInstance(appDatabase.pageDao())
        bind(HistoryDao::class.java).toInstance(appDatabase.historyDao())
        bind(TaskDao::class.java).toInstance(appDatabase.taskDao())
    }

    private fun providesAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "mangas").build()
    }
}