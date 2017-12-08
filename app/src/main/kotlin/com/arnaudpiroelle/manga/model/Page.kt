package com.arnaudpiroelle.manga.model

import android.os.Parcel
import android.os.Parcelable

class Page : Parcelable {
    var mangaAlias: String? = null
    var chapterNumber: String? = null
    var pageNumber: String? = null
    var pageName: String? = null
    var extension: String? = null

    constructor() {

    }

    private constructor(`in`: Parcel) {
        mangaAlias = `in`.readString()
        chapterNumber = `in`.readString()
        pageNumber = `in`.readString()
        pageName = `in`.readString()
        extension = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(mangaAlias)
        dest.writeString(chapterNumber)
        dest.writeString(pageNumber)
        dest.writeString(pageName)
        dest.writeString(extension)
    }

    public class PageBuilder {
        private var page: Page = Page()

        public fun withPageNumber(pageNumber: String): PageBuilder {
            page.pageNumber = pageNumber
            return this
        }

        public fun withMangaAlias(mangaAlias: String): PageBuilder {
            page.mangaAlias = mangaAlias
            return this
        }

        public fun withChapterNumber(chapterNumber: String): PageBuilder {
            page.chapterNumber = chapterNumber
            return this
        }

        public fun withExtenstion(extension: String): PageBuilder {
            page.extension = extension
            return this
        }

        public fun withName(name: String): PageBuilder {
            page.pageName = name
            return this
        }

        public fun build(): Page {
            return page;
        }

        companion object {
            public fun createPage(): PageBuilder {
                return PageBuilder();
            }
        }
    }

    companion object {

        val CREATOR: Parcelable.Creator<Page> = object : Parcelable.Creator<Page> {
            override fun createFromParcel(`in`: Parcel): Page {
                return Page(`in`)
            }

            override fun newArray(size: Int): Array<Page?> {
                return arrayOfNulls(size)
            }
        }
    }
}
