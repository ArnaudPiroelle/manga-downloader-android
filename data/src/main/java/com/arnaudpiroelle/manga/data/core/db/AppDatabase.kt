package com.arnaudpiroelle.manga.data.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arnaudpiroelle.manga.data.core.db.converter.ChapterStatusConverter
import com.arnaudpiroelle.manga.data.core.db.converter.PostProcessConverter
import com.arnaudpiroelle.manga.data.core.db.converter.StatusConverter
import com.arnaudpiroelle.manga.data.core.db.converter.TypeConverter
import com.arnaudpiroelle.manga.data.core.db.dao.*
import com.arnaudpiroelle.manga.data.model.*


@Database(
        entities = [
            Manga::class,
            Chapter::class,
            Page::class,
            History::class,
            Task::class
        ],
        version = 1)
@TypeConverters(
        value = [
            StatusConverter::class,
            TypeConverter::class,
            PostProcessConverter::class,
            ChapterStatusConverter::class
        ])
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun historyDao(): HistoryDao
    abstract fun taskDao(): TaskDao
    abstract fun chapterDao(): ChapterDao
    abstract fun pageDao(): PageDao
}