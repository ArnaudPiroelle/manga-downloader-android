package com.arnaudpiroelle.manga.ui.explorer

interface ExplorerContract {
    interface View {
        fun displayMangas()
        fun displayHistory()
        fun displaySettings()
        fun selectDestinationFolder()
        fun displayTasks()
    }

    interface UserActionsListener {
        fun navigateTo(itemId: Int): Boolean
    }
}