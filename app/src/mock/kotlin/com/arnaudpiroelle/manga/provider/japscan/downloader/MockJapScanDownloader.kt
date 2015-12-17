package com.arnaudpiroelle.manga.provider.japscan.downloader

import android.content.Context
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page
import java.io.File
import java.io.InputStream

class MockJapScanDownloader(val context: Context) : MangaProvider {
    override val name: String
        get() = "MockJapScan"

    override fun findMangas(): List<Manga> {
        val naruto =   Manga.MangaBuilder.createManga().withMangaAlias("naruto").withName("Naruto").withProvider(name).build()
        val onepiece = Manga.MangaBuilder.createManga().withMangaAlias("one-piece").withName("One Piece").withProvider(name).build()

        return listOf(naruto, onepiece)
    }

    override fun findChapters(manga: Manga): List<Chapter> {
        val chapter1 = Chapter.ChapterBuilder.createChapter().withMangaAlias(manga.mangaAlias!!).withChapterNumber("699").build()
        val chapter2 = Chapter.ChapterBuilder.createChapter().withMangaAlias(manga.mangaAlias!!).withChapterNumber("700").build()

        return listOf(chapter1, chapter2)
    }

    override fun findPages(chapter: Chapter): List<Page> {
        val page = Page.PageBuilder.createPage().withExtenstion("jpg").build()

        return listOf(page)
    }

    override fun findPage(page: Page): InputStream {
        return context.resources.openRawResource(R.raw.page_01)
    }

    override fun postProcess(file: File) {

    }

}
