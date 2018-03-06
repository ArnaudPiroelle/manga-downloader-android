package com.arnaudpiroelle.manga.model.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity
class Manga() : Comparable<Manga>, Parcelable {

    constructor(name: String, alias: String, provider: String) : this() {
        this.name = name
        this.mangaAlias = alias
        this.provider = provider
        this.lastChapter = ""
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "mangaAlias")
    var mangaAlias: String? = null

    @ColumnInfo(name = "provider")
    var provider: String? = null

    @ColumnInfo(name = "lastChapter")
    var lastChapter: String? = null

    private constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
        mangaAlias = parcel.readString()
        provider = parcel.readString()
        lastChapter = parcel.readString()
    }


    override fun compareTo(other: Manga): Int {
        return name!!.trim { it <= ' ' }.compareTo(other.name!!)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(mangaAlias)
        dest.writeString(provider)
        dest.writeString(lastChapter)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Manga

        if (id != other.id) return false
        if (name != other.name) return false
        if (mangaAlias != other.mangaAlias) return false
        if (provider != other.provider) return false
        if (lastChapter != other.lastChapter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (mangaAlias?.hashCode() ?: 0)
        result = 31 * result + (provider?.hashCode() ?: 0)
        result = 31 * result + (lastChapter?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Manga(id=$id, name=$name, mangaAlias=$mangaAlias, provider=$provider, lastChapter=$lastChapter)"
    }

    companion object {
        val CREATOR: Parcelable.Creator<Manga> = object : Parcelable.Creator<Manga> {
            override fun createFromParcel(`in`: Parcel): Manga {
                return Manga(`in`)
            }

            override fun newArray(size: Int): Array<Manga?> {
                return arrayOfNulls(size)
            }
        }
    }
}
