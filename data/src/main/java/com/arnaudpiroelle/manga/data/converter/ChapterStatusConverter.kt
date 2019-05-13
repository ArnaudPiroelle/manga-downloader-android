package com.arnaudpiroelle.manga.data.converter

import androidx.room.TypeConverter
import com.arnaudpiroelle.manga.data.model.Chapter

class ChapterStatusConverter {
    @TypeConverter
    fun fromString(status: String): Chapter.Status {
        return Chapter.Status.valueOf(status)
    }

    @TypeConverter
    fun toString(status: Chapter.Status): String {
        return status.name
    }
}