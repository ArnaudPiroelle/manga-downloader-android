package com.arnaudpiroelle.manga.ui.explorer

import android.Manifest
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.ui.history.HistoryFragment
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingFragment
import com.arnaudpiroelle.manga.ui.settings.SettingsFragment
import com.codekidlabs.storagechooser.StorageChooser
import com.codekidlabs.storagechooser.StorageChooser.DIRECTORY_CHOOSER
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_explorer.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import javax.inject.Inject


@RuntimePermissions
class ExplorerActivity : AppCompatActivity(),
        ExplorerContract.View,
        BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private val userActionsListener: ExplorerContract.UserActionsListener by lazy { ExplorerPresenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explorer)

        inject()

        navigation.setOnNavigationItemSelectedListener(this)

        if (preferencesHelper.getOutputFolder() == null) {
            selectDestinationFolderWithPermissionCheck()
        }

        if (savedInstanceState == null) {
            userActionsListener.navigateTo(navigation.selectedItemId)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        onRequestPermissionsResult(requestCode, grantResults)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId != navigation.selectedItemId) {
            return userActionsListener.navigateTo(item.itemId)
        }

        return false
    }

    override fun displayMangas() {
        replace(MangaListingFragment())
    }

    override fun displayHistory() {
        replace(HistoryFragment())
    }

    override fun displaySettings() {
        replace(SettingsFragment())
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override fun selectDestinationFolder() {
        val chooser = StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                //.withPredefinedPath("")
                .setType(DIRECTORY_CHOOSER)
                .allowAddFolder(true)
                .allowCustomPath(true)
                .build()

        chooser.setOnSelectListener {
            preferencesHelper.setOutputFolder(it)
        }

        chooser.show()
    }

    private fun replace(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content, fragment)
                .commit()
    }
}
