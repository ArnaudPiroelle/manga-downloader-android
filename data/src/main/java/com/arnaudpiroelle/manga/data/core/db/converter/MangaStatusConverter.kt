package com.arnaudpiroelle.manga.data.core.db.converter

import androidx.room.TypeConverter
import com.arnaudpiroelle.manga.data.model.Manga

class MangaStatusConverter {
    @TypeConverter
    fun fromString(status: String): Manga.Status {
        return Manga.Status.valueOf(status)
    }

    @TypeConverter
    fun toString(status: Manga.Status): String {
        return status.name
    }
}