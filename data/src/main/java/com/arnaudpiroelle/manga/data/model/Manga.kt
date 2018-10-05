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
        @ColumnInfo(name = "provider") val provider: String = "",
        @ColumnInfo(name = "name") val name: String = "",
        @ColumnInfo(name = "alias") val alias: String = "")


data class MangaWithCover(
        val id: Long = 0,
        val name: String = "",
        val image: String? = ""
)