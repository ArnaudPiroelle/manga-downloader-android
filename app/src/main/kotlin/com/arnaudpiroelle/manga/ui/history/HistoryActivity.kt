package com.arnaudpiroelle.manga.ui.history

import android.os.Bundle
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.ui.manga.NavigationActivity


class HistoryActivity : NavigationActivity(R.id.nav_history) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        replaceFragment(HistoryFragment(), false)
    }
}