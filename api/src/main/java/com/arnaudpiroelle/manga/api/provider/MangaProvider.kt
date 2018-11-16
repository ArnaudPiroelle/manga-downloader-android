package com.arnaudpiroelle.manga.api.provider

import com.arnaudpiroelle.manga.api.model.Chapter
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.api.model.Page
import com.arnaudpiroelle.manga.api.model.PostProcessType
import okhttp3.Response
import java.io.File

interface MangaProvider {
    fun findMangas(): List<Manga>

    fun findChapters(mangaAlias: String): List<Chapter>

    fun findPages(mangaAlias: String, chapterNumber: String): List<Page>

    fun findPage(pageUrl: String): Response

    fun postProcess(postProcessType: PostProcessType, page: File)
}
