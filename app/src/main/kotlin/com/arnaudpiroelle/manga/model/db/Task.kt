package com.arnaudpiroelle.manga.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "status") var status: Status = Status.NEW,
        @ColumnInfo(name = "type") var type: Type = Type.MANGA_METADATA,
        @ColumnInfo(name = "item") var item: Long = 0) {

    enum class Status {
        NEW,
        SUCCESS,
        ERROR
    }

    enum class Type {
        MANGA_METADATA,
        RETRIEVE_CHAPTERS,
        DOWNLOAD_CHAPTER
    }
}
