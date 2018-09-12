package com.arnaudpiroelle.manga.ui.manga.modify

import com.arnaudpiroelle.manga.api.model.Chapter

interface ModifyMangaContract {
    interface View {
        fun displayChapters(chapters: List<Chapter>, selection: Int)
        fun closeWizard()
    }

    interface UserActionsListener {
        fun findChapters(provider: String, mangaAlias: String, lastChapter: String)
        fun selectChapter(provider: String, name: String, mangaAlias: String, chapterNumber: String)
    }
}