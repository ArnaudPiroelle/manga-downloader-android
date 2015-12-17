package com.arnaudpiroelle.manga.ui.manga

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.ui.history.HistoryActivity
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingActivity
import com.arnaudpiroelle.manga.ui.settings.SettingsActivity
import kotlinx.android.synthetic.activity_navigation.*
import kotlinx.android.synthetic.include_navigation.*

open class NavigationActivity(var mNavItemId : Int) : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_navigation)

        GRAPH.inject(this)


        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        with(nav_view){
            menu.findItem(mNavItemId).setCheckable(true)
            menu.findItem(mNavItemId).setChecked(true)
        }

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(NAV_ITEM_ID, mNavItemId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun navigate(navIdRes: Int) {
        when (navIdRes) {
            R.id.nav_mymangas ->  startActivity(Intent(this, MangaListingActivity::class.java))
            R.id.nav_history ->  startActivity(Intent(this, HistoryActivity::class.java))
            R.id.nav_settings ->  startActivity(Intent(this, SettingsActivity::class.java))
        }

    }


    protected fun replaceFragment(fragment: Fragment, addToBackStack: Boolean?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction().replace(R.id.content_nav, fragment, fragment.tag)

        if (addToBackStack!!) {
            fragmentTransaction.addToBackStack(fragment.tag)
        }

        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        if (mNavItemId != menuItem.itemId) {
            navigate(menuItem.itemId)
        }

        drawer_layout.closeDrawers()
        return true
    }

    companion object {
        private val NAV_ITEM_ID = "NAV_ITEM_ID"
    }
}
