package com.arnaudpiroelle.manga.data.core.db.converter

import androidx.room.TypeConverter
import com.arnaudpiroelle.manga.data.model.Task

class TypeConverter {
    @TypeConverter
    fun fromString(type: String): Task.Type {
        return Task.Type.valueOf(type)
    }

    @TypeConverter
    fun toString(type: Task.Type): String {
        return type.name
    }
}