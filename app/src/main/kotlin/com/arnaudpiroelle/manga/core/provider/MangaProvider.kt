package com.arnaudpiroelle.manga.core.provider

import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.model.network.Chapter
import com.arnaudpiroelle.manga.model.network.Page
import io.reactivex.Single
import okhttp3.Response

interface MangaProvider {
    fun findMangas(): Single<List<Manga>>

    fun findChapters(manga: Manga): Single<List<Chapter>>

    fun findPages(manga: Manga, chapter: Chapter): Single<List<Page>>

    fun findPage(page: Page): Single<Response>
}
