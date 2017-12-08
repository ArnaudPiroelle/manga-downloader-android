package com.arnaudpiroelle.manga.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Chapter : Parcelable {
    var name: String? = null
    var mangaAlias: String? = null
    var chapterNumber: String? = null

    var pages: List<Page>? = null

    public constructor()

    private constructor(`in`: Parcel) {
        name = `in`.readString()
        mangaAlias = `in`.readString()
        chapterNumber = `in`.readString()

        pages = ArrayList<Page>()
        `in`.readTypedList(pages, Page.CREATOR)
    }

    fun computeChapterFolderPath(baseFolder: String): String {
        return "$baseFolder/$mangaAlias/$chapterNumber"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(mangaAlias)
        dest.writeString(chapterNumber)
        dest.writeTypedList(pages)
    }

    public class ChapterBuilder {
        var chapter: Chapter = Chapter()

        public fun withName(name: String): ChapterBuilder {
            chapter.name = name
            return this
        }

        public fun withMangaAlias(mangaAlias: String): ChapterBuilder {
            chapter.mangaAlias = mangaAlias
            return this
        }

        public fun withChapterNumber(chapterNumber: String): ChapterBuilder {
            chapter.chapterNumber = chapterNumber
            return this
        }

        public fun build(): Chapter {
            return chapter
        }

        companion object {
            public fun createChapter(): ChapterBuilder {
                return ChapterBuilder();
            }
        }
    }

    companion object {

        val CREATOR: Parcelable.Creator<Chapter> = object : Parcelable.Creator<Chapter> {
            override fun createFromParcel(`in`: Parcel): Chapter {
                return Chapter(`in`)
            }

            override fun newArray(size: Int): Array<Chapter?> {
                return arrayOfNulls(size)
            }
        }
    }
}
