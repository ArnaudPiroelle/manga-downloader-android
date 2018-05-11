package com.arnaudpiroelle.manga.ui.manga.modify

import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.model.network.Chapter

interface ModifyMangaContract {
    interface View {
        fun displayChapters(chapters: List<Chapter>, selection: Int)
        fun closeWizard()
    }

    interface UserActionsListener {
        fun findChapters(manga: Manga)
        fun selectChapter(manga: Manga, chapter: Chapter)
    }
}