package com.arnaudpiroelle.manga.core.db.converter

import androidx.room.TypeConverter
import com.arnaudpiroelle.manga.model.db.Task.Status

class StatusConverter {
    @TypeConverter
    fun fromString(status: String): Status {
        return Status.valueOf(status)
    }

    @TypeConverter
    fun toString(status: Status): String {
        return status.name
    }
}