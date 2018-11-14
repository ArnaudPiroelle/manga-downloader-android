package com.arnaudpiroelle.manga.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arnaudpiroelle.manga.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.internal.NavigationMenuView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_bottom_navigation_drawer.*

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.AppTheme_BottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_navigation_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigation_view.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.navigation_mangas -> navigateTo(R.id.myMangasFragment)
                R.id.navigation_history -> navigateTo(R.id.historyFragment)
                R.id.navigation_tasks -> navigateTo(R.id.tasksFragment)
                R.id.navigation_settings -> navigateTo(R.id.settingsFragment)
            }

            dismiss()
            true
        }

        val navController = findNavController()
        val destinationId = navController.currentDestination?.id ?: -1
        val menu = navigation_view.menu
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)

            when (item.itemId) {
                R.id.navigation_mangas -> item.isChecked = destinationId == R.id.myMangasFragment
                R.id.navigation_history -> item.isChecked = destinationId == R.id.historyFragment
                R.id.navigation_tasks -> item.isChecked = destinationId == R.id.tasksFragment
                R.id.navigation_settings -> item.isChecked = destinationId == R.id.settingsFragment
            }
        }

        disableNavigationViewScrollbars(navigation_view)
    }

    private fun navigateTo(@IdRes destinationId: Int) {

        val navController = findNavController()
        val currentId = navController.currentDestination?.id ?: -1

        val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .setClearTask(true)

        val options = builder.build()
        try {
            if (currentId != destinationId) {
                navController.navigate(destinationId, null, options)
            }
        } catch (e: IllegalArgumentException) {

        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { newDialog ->
            val d = newDialog as BottomSheetDialog

            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> dismiss()
                    }
                }
            })
        }

        return dialog
    }

    private fun disableNavigationViewScrollbars(navigationView: NavigationView?) {
        val navigationMenuView = navigationView?.getChildAt(0) as NavigationMenuView
        navigationMenuView.isVerticalScrollBarEnabled = false
    }


}