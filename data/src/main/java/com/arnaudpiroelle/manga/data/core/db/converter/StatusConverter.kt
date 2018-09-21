package com.arnaudpiroelle.manga.data.core.db.converter

import androidx.room.TypeConverter
import com.arnaudpiroelle.manga.data.model.Task

class StatusConverter {
    @TypeConverter
    fun fromString(status: String): Task.Status {
        return Task.Status.valueOf(status)
    }

    @TypeConverter
    fun toString(status: Task.Status): String {
        return status.name
    }
}