package com.arnaudpiroelle.manga.data

import androidx.room.Room
import com.arnaudpiroelle.manga.data.core.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val dataModule = module {
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "mangas").build() }
    single { get<AppDatabase>().mangaDao() }
    single { get<AppDatabase>().chapterDao() }
    single { get<AppDatabase>().historyDao() }
}