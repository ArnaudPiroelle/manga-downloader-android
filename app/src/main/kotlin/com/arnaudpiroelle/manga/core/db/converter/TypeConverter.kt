package com.arnaudpiroelle.manga.core.db.converter

import androidx.room.TypeConverter
import com.arnaudpiroelle.manga.model.db.Task

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