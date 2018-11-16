package com.arnaudpiroelle.manga.core.utils

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.arnaudpiroelle.manga.ui.drawer.BottomNavigationDrawerFragment


fun Fragment.setActionBar(bar: Toolbar) {
    if (activity?.menuInflater != null) {
        onCreateOptionsMenu(bar.menu, activity?.menuInflater)
        bar.setOnMenuItemClickListener { item -> onOptionsItemSelected(item) }
    }

    bar.setNavigationOnClickListener {
        BottomNavigationDrawerFragment().show(childFragmentManager, "drawer")
    }
}