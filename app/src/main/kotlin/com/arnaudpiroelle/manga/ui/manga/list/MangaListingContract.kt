package com.arnaudpiroelle.manga.ui.manga.list

import com.arnaudpiroelle.manga.data.model.Manga

interface MangaListingContract {
    interface View {
        fun displayMangas(mangas: List<Manga>)
        fun openNewMangaWizard()
        fun displayModificationDialog(manga: Manga)
        fun displayRemoveConfirmation(manga: Manga)
    }

    interface UserActionsListener {
        fun register()
        fun unregister()
        fun addManga()
        fun askForRemove(manga: Manga)
        fun askForModify(manga: Manga)
        fun remove(manga: Manga)
    }
}