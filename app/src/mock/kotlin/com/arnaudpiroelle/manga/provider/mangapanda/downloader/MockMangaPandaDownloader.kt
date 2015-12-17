package com.arnaudpiroelle.manga.provider.mangapanda.downloader

import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page
import java.io.File
import java.io.InputStream


class MockMangaPandaDownloader : MangaProvider {
    override val name: String
        get() = "MockMangaPanda"

    override fun findMangas(): List<Manga> {
        return listOf()
    }

    override fun findChapters(manga: Manga): List<Chapter> {
        return listOf()
    }

    override fun findPages(chapter: Chapter): List<Page> {
        return listOf()
    }

    override fun findPage(page: Page): InputStream {
        throw UnsupportedOperationException()
    }

    override fun postProcess(file: File) {

    }
}