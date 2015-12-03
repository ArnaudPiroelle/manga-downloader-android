package com.arnaudpiroelle.manga.model

import android.os.Parcel
import android.os.Parcelable

import java.util.ArrayList

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
