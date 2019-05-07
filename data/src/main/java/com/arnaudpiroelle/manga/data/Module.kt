package com.arnaudpiroelle.manga.data

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module(createdAtStart = true) {
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "mangas").build() }
    single { get<AppDatabase>().mangaDao() }
    single { get<AppDatabase>().chapterDao() }
    single { get<AppDatabase>().historyDao() }
}