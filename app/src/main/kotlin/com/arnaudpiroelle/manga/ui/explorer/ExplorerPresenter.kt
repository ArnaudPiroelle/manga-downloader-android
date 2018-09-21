package com.arnaudpiroelle.manga.ui.explorer

import com.arnaudpiroelle.manga.R

class ExplorerPresenter(val view: ExplorerContract.View) : ExplorerContract.UserActionsListener {
    override fun navigateTo(itemId: Int): Boolean {
        return when (itemId) {
            R.id.navigation_mangas -> {
                view.displayMangas()
                true
            }

            R.id.navigation_history -> {
                view.displayHistory()
                true
            }

            R.id.navigation_settings -> {
                view.displaySettings()
                true
            }

            R.id.navigation_tasks -> {
                view.displayTasks()
                true
            }
            else -> false
        }
    }
}