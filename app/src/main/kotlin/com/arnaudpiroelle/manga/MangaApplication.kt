package com.arnaudpiroelle.manga

import android.app.Application
import android.database.sqlite.SQLiteDatabase

import com.arnaudpiroelle.manga.core.inject.DaggerMangaComponent
import com.arnaudpiroelle.manga.core.inject.MangaComponent
import com.arnaudpiroelle.manga.core.inject.module.ApplicationModule
import com.arnaudpiroelle.manga.service.DownloadService
import com.crashlytics.android.Crashlytics

import io.fabric.sdk.android.Fabric
import se.emilsjolander.sprinkles.Migration
import se.emilsjolander.sprinkles.Sprinkles

class MangaApplication : Application() {

    companion object {
        lateinit var GRAPH: MangaComponent
    }

    override fun onCreate() {
        super.onCreate()

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }

        DownloadService.updateScheduling(this)

        GRAPH = DaggerMangaComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()

        val sprinkles = Sprinkles.init(this)

        sprinkles.addMigration(object : Migration() {
            override fun doMigration(db: SQLiteDatabase) {
                db.execSQL("CREATE TABLE Mangas (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "mangaAlias TEXT," +
                        "provider TEXT," +
                        "lastChapter TEXT" +
                        ")")
            }
        })

        sprinkles.addMigration(object : Migration() {
            override fun doMigration(db: SQLiteDatabase) {
                db.execSQL(
                        "CREATE TABLE Histories (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "label TEXT," +
                                "date INTEGER" +
                                ")")
            }
        })

    }

}
