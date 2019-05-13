package com.arnaudpiroelle.manga.api.provider

import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.*
import okhttp3.ResponseBody
import java.io.File

@Suppress("LeakingThis")
abstract class MangaProvider(registry: ProviderRegistry) {

    init {
        registry.register(getName(), this)
    }

    abstract fun getName(): String

    abstract suspend fun findMangas(): List<Manga>

    abstract suspend fun findDetails(mangaAlias: String): MangaDetails

    abstract suspend fun findChapters(mangaAlias: String): List<Chapter>

    abstract suspend fun findPages(mangaAlias: String, chapterNumber: String): List<Page>

    abstract suspend fun findPage(pageUrl: String): ResponseBody

    abstract suspend fun postProcess(postProcessType: PostProcessType, page: File)
}
