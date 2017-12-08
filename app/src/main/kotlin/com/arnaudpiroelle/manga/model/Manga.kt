package com.arnaudpiroelle.manga.model

import android.os.Parcel
import android.os.Parcelable
import se.emilsjolander.sprinkles.Model
import se.emilsjolander.sprinkles.annotations.AutoIncrement
import se.emilsjolander.sprinkles.annotations.Column
import se.emilsjolander.sprinkles.annotations.Key
import se.emilsjolander.sprinkles.annotations.Table
import java.util.*

@Table("Mangas")
class Manga : Model, Comparable<Manga>, Parcelable {

    public constructor() {

    }

    private constructor(`in`: Parcel) {
        id = `in`.readLong()
        name = `in`.readString()
        mangaAlias = `in`.readString()
        provider = `in`.readString()
        lastChapter = `in`.readString()

        chapters = ArrayList<Chapter>()
        `in`.readTypedList(chapters, Chapter.CREATOR)
    }

    @Key
    @AutoIncrement
    @Column("id")
    var id: Long = 0

    @Column("name")
    var name: String? = null

    @Column("mangaAlias")
    var mangaAlias: String? = null

    @Column("provider")
    var provider: String? = null

    @Column("lastChapter")
    var lastChapter: String? = null

    var chapters: List<Chapter>? = null

    override fun compareTo(another: Manga): Int {
        return name!!.trim { it <= ' ' }.compareTo(another.name!!)
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
        dest.writeTypedList(chapters)
    }

    public class MangaBuilder {
        var manga: Manga = Manga()

        public fun withName(name: String): MangaBuilder {
            manga.name = name
            return this
        }

        public fun withMangaAlias(mangaAlias: String): MangaBuilder {
            manga.mangaAlias = mangaAlias
            return this
        }

        public fun withProvider(provider: String): MangaBuilder {
            manga.provider = provider
            return this
        }

        public fun withLastChapter(lastChapter: String): MangaBuilder {
            manga.lastChapter = lastChapter
            return this
        }

        public fun withId(id: Long): MangaBuilder {
            manga.id = id
            return this
        }

        public fun build(): Manga {
            return manga!!
        }

        companion object {
            public fun createManga(): MangaBuilder {
                return MangaBuilder();
            }
        }
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
