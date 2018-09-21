package com.arnaudpiroelle.manga.data.core.db.converter

import androidx.room.TypeConverter
import com.arnaudpiroelle.manga.data.model.Page

class PostProcessConverter {
    @TypeConverter
    fun fromString(postProcess: String): Page.PostProcess {
        return Page.PostProcess.valueOf(postProcess)
    }

    @TypeConverter
    fun toString(postProcess: Page.PostProcess): String {
        return postProcess.name
    }
}