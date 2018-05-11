package com.arnaudpiroelle.manga.core.inject.module

import android.arch.persistence.room.Room
import android.content.Context
import com.arnaudpiroelle.manga.core.db.AppDatabase
import com.arnaudpiroelle.manga.core.db.HistoryDao
import com.arnaudpiroelle.manga.core.db.MangaDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun providesAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "mangas").build()
    }

    @Provides
    @Singleton
    fun providesMangaDao(appDatabase: AppDatabase): MangaDao {
        return appDatabase.mangaDao()
    }

    @Provides
    @Singleton
    fun providesHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
    }
}