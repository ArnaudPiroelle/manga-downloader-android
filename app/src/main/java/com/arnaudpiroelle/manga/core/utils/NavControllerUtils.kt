package com.arnaudpiroelle.manga.core.utils

import android.text.TextUtils
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.arnaudpiroelle.manga.R
import com.google.android.material.bottomappbar.BottomAppBar
import java.lang.ref.WeakReference


fun BottomAppBar.setupNavController(navController: NavController) {
    navController.addOnNavigatedListener(ToolbarOnNavigatedListener(this))
}

class ToolbarOnNavigatedListener(toolbar: Toolbar) : NavController.OnNavigatedListener {
    private val toolbarWeakReference = WeakReference(toolbar)

    override fun onNavigated(controller: NavController, destination: NavDestination) {
        val toolbar = toolbarWeakReference.get()
        if (toolbar == null) {
            controller.removeOnNavigatedListener(this)
            return
        }

        val title = destination.label
        if (!TextUtils.isEmpty(title)) {
            toolbar.title = title
        }

        val isRootDestination = when (destination.id) {
            R.id.navigation_mangas,
            R.id.navigation_history,
            R.id.navigation_tasks,
            R.id.navigation_settings -> true
            else -> false
        }
        toolbar.navigationIcon = if (isRootDestination) {
            null
        } else {
            val drawerArrowDrawable = DrawerArrowDrawable(toolbar.context)
            drawerArrowDrawable.progress = 1.0f
            drawerArrowDrawable
        }
    }

}
