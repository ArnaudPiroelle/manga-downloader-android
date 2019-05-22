package com.arnaudpiroelle.manga.data.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE History ADD COLUMN sublabel TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE mangas SET thumbnail = replace(thumbnail, 'http://seed.arnaudpiroelle.com:3000/', 'https://japscan-proxy.herokuapp.com/')")
    }

}