package com.arnaudpiroelle.manga.model.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
class History() {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "label")
    var label: String? = null

    @ColumnInfo(name = "date")
    var date: Long = 0

    constructor(label: String) : this() {
        this.label = label
        this.date = Date().time
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as History

        if (id != other.id) return false
        if (label != other.label) return false
        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (label?.hashCode() ?: 0)
        result = 31 * result + date.hashCode()
        return result
    }

    override fun toString(): String {
        return "History(id=$id, label=$label, date=$date)"
    }


}
