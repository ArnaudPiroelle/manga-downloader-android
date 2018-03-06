package com.arnaudpiroelle.manga.core.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.arnaudpiroelle.manga.model.db.History
import com.arnaudpiroelle.manga.model.db.Manga


@Database(entities = [(Manga::class), (History::class)], version = 1 )
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun historyDao(): HistoryDao
}