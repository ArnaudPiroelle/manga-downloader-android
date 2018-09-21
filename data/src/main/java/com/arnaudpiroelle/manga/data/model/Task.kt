package com.arnaudpiroelle.manga.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "status") val status: Status = Status.NEW,
        @ColumnInfo(name = "type") val type: Type = Type.RETRIEVE_CHAPTERS,
        @ColumnInfo(name = "item") val item: Long) {

    enum class Status {
        NEW,
        IN_PROGRESS,
        SUCCESS,
        ERROR
    }

    enum class Type {
        RETRIEVE_CHAPTERS,
        DOWNLOAD_CHAPTER
    }
}
