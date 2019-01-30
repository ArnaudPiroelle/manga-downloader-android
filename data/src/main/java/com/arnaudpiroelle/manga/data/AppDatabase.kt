package com.arnaudpiroelle.manga.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arnaudpiroelle.manga.data.converter.ChapterStatusConverter
import com.arnaudpiroelle.manga.data.converter.MangaStatusConverter
import com.arnaudpiroelle.manga.data.dao.ChapterDao
import com.arnaudpiroelle.manga.data.dao.HistoryDao
import com.arnaudpiroelle.manga.data.dao.MangaDao
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.History
import com.arnaudpiroelle.manga.data.model.Manga


@Database(
        entities = [
            Manga::class,
            Chapter::class,
            History::class
        ],
        version = 1)
@TypeConverters(
        value = [
            ChapterStatusConverter::class,
            MangaStatusConverter::class
        ])
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun historyDao(): HistoryDao
    abstract fun chapterDao(): ChapterDao
}