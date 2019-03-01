package com.arnaudpiroelle.manga.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "mangas",
        indices = [
            Index("alias"),
            Index("provider")
        ])
data class Manga(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "provider") val provider: String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "alias") val alias: String,
        @ColumnInfo(name = "thumbnail") val thumbnail: String = "",
        @ColumnInfo(name = "status") val status: Status,
        @ColumnInfo(name = "origin") val origin: String = "",
        @ColumnInfo(name = "year") val year: String = "",
        @ColumnInfo(name = "type") val type: String = "",
        @ColumnInfo(name = "kind") val kind: String = "",
        @ColumnInfo(name = "author") val author: String = "",
        @ColumnInfo(name = "summary") val summary: String = "") {

    fun cleanName(): String {
        return name.replace("/", "-")
    }

    enum class Status {
        ADDED,
        INITIALIZED,
        ENABLED,
        DISABLED
    }
}