package com.arnaudpiroelle.manga.data

import androidx.room.Room
import com.arnaudpiroelle.manga.data.migration.MIGRATION_1_2
import com.arnaudpiroelle.manga.data.migration.MIGRATION_2_3
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module(createdAtStart = true) {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "mangas")
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()
    }
    single { get<AppDatabase>().mangaDao() }
    single { get<AppDatabase>().chapterDao() }
    single { get<AppDatabase>().historyDao() }
}