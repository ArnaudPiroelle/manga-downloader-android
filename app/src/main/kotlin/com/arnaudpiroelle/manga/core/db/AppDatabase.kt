package com.arnaudpiroelle.manga.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arnaudpiroelle.manga.core.db.converter.StatusConverter
import com.arnaudpiroelle.manga.core.db.converter.TypeConverter
import com.arnaudpiroelle.manga.core.db.dao.HistoryDao
import com.arnaudpiroelle.manga.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.core.db.dao.TaskDao
import com.arnaudpiroelle.manga.model.db.History
import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.model.db.Task


@Database(entities = [Manga::class, History::class, Task::class], version = 2)
@TypeConverters(value = [StatusConverter::class, TypeConverter::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun historyDao(): HistoryDao
    abstract fun taskDao(): TaskDao
}