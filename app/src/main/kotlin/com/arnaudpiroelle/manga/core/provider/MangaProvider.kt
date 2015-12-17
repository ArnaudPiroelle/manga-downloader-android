package com.arnaudpiroelle.manga.core.provider

import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page
import java.io.File

import java.io.InputStream

interface MangaProvider {

    val name: String

    fun findMangas(): List<Manga>

    fun findChapters(manga: Manga): List<Chapter>

    fun findPages(chapter: Chapter): List<Page>

    fun findPage(page: Page): InputStream

    fun postProcess(file: File): Unit
}
