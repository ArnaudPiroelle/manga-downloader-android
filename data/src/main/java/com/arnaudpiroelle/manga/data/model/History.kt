package com.arnaudpiroelle.manga.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class History(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo(name = "label") val label: String? = null,
        @ColumnInfo(name = "sublabel") val sublabel: String? = null,
        @ColumnInfo(name = "date") val date: Date = Date())