package com.arnaudpiroelle.manga.data.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "chapters",
        indices = [
            Index("mangaId")
        ],
        foreignKeys = [
            ForeignKey(entity = Manga::class,
                    parentColumns = ["id"],
                    childColumns = ["mangaId"],
                    onDelete = CASCADE)])
data class Chapter(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "number") val number: String,
        @ColumnInfo(name = "status") val status: Status,
        @ColumnInfo(name = "mangaId") val mangaId: Long) {

    enum class Status {
        WANTED,
        SKIPPED,
        DOWNLOADING,
        DOWNLOADED,
        ERROR
    }
}
