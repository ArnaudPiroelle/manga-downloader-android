package com.arnaudpiroelle.manga.ui.manga.add

import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.api.provider.MangaProvider

interface AddMangaContract {
    interface View {
        fun displayProviders(providers: Map<String, MangaProvider>)
        fun displayMangas(mangas: List<Manga>)
        fun filterMangasBy(name: String)
        fun displayChapters(manga: Manga)
    }

    interface UserActionsListener {
        fun fillProviders()
        fun findMangas(provider: MangaProvider)
        fun filterBy(name: String)
        fun selectManga(manga: Manga)
    }
}