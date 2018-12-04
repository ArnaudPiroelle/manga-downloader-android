package com.arnaudpiroelle.manga.data.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arnaudpiroelle.manga.data.core.db.converter.ChapterStatusConverter
import com.arnaudpiroelle.manga.data.core.db.converter.PostProcessConverter
import com.arnaudpiroelle.manga.data.core.db.dao.ChapterDao
import com.arnaudpiroelle.manga.data.core.db.dao.HistoryDao
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.core.db.dao.PageDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.History
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.data.model.Page


@Database(
        entities = [
            Manga::class,
            Chapter::class,
            Page::class,
            History::class
        ],
        version = 1)
@TypeConverters(
        value = [
            PostProcessConverter::class,
            ChapterStatusConverter::class
        ])
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun historyDao(): HistoryDao
    abstract fun chapterDao(): ChapterDao
    abstract fun pageDao(): PageDao
}