package com.arnaudpiroelle.manga.ui.home

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment


fun Fragment.setActionBar(bar: Toolbar) {
    if (activity?.menuInflater != null) {
        onCreateOptionsMenu(bar.menu, activity?.menuInflater)
        bar.setOnMenuItemClickListener { item -> onOptionsItemSelected(item) }
    }

    bar.setNavigationOnClickListener {
        BottomNavigationDrawerFragment().show(childFragmentManager, "drawer")
    }
}