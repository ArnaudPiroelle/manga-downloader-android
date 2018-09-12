package com.arnaudpiroelle.manga.api.provider

import com.arnaudpiroelle.manga.api.model.Chapter
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.api.model.Page
import io.reactivex.Single
import okhttp3.Response

interface MangaProvider {
    fun findMangas(): Single<List<Manga>>

    fun findChapters(mangaAlias: String): Single<List<Chapter>>

    fun findPages(mangaAlias: String, chapterNumber: String): Single<List<Page>>

    fun findPage(pageUrl: String): Single<Response>
}
