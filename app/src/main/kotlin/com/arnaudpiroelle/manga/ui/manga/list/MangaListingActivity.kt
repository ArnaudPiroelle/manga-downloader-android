package com.arnaudpiroelle.manga.ui.manga.list

import android.os.Bundle
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.ui.manga.NavigationActivity

class MangaListingActivity : NavigationActivity(R.id.nav_mymangas) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        replaceFragment(MangaListingFragment(), false)
    }
}

