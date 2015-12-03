package com.arnaudpiroelle.manga.ui.manga.add

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.permission.PermissionHelper
import com.arnaudpiroelle.manga.event.ChapterSelectedEvent
import com.arnaudpiroelle.manga.event.MangaSelectedEvent
import com.arnaudpiroelle.manga.event.ProviderSelectedEvent
import com.arnaudpiroelle.manga.model.History
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.service.DownloadService
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingFragment
import kotlinx.android.synthetic.activity_add_manga.*
import java.util.*

class AddMangaActivity : AppCompatActivity() {

    lateinit private var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_manga)

        GRAPH.inject(this)

        permissionHelper = PermissionHelper(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        displayProviders()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun replace(contentId: Int, fragment: Fragment, addToBackStack: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction().replace(contentId, fragment, fragment.tag)

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.tag)
        }

        fragmentTransaction.commit()
    }

    private fun displayProviders() {
        replace(R.id.add_content, AddMangaFragment(), false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MangaListingFragment.MY_PERMISSIONS_REQUEST_WRITE_STORAGE -> {
                startDownloadService()
            }
        }
    }

    private fun startDownloadService() {
        startService(Intent(this, DownloadService::class.java))
        finish()
    }
}
