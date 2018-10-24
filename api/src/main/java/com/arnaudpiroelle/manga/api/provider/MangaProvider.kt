package com.arnaudpiroelle.manga.api.provider

import com.arnaudpiroelle.manga.api.model.Chapter
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.api.model.Page
import okhttp3.Response

interface MangaProvider {
    fun findMangas(): List<Manga>

    fun findChapters(mangaAlias: String): List<Chapter>

    fun findPages(mangaAlias: String, chapterNumber: String): List<Page>

    fun findPage(pageUrl: String): Response
}
