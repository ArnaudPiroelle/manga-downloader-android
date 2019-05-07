package com.arnaudpiroelle.manga.ui

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation.findNavController
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.worker.utils.PreferencesHelper
import com.arnaudpiroelle.manga.worker.utils.isFromExternalStorage
import com.codekidlabs.storagechooser.StorageChooser
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {
    private val preferencesHelper: PreferencesHelper by inject()

    private val navController by lazy { findNavController(this, R.id.nav_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        val outputFolder = preferencesHelper.getOutputFolder()
        if (outputFolder == null) {
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE_AND_SHOW_STORAGE)
            } else {
                showStorageChooser()
            }
        } else {
            val isFromExternalStorage = outputFolder.isFromExternalStorage()
            if (isFromExternalStorage && checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE_ONLY)
            }
        }
    }

    fun showStorageChooser() {
        //TODO: Change chooser that don't use fragmentManager to be compliant with androidX
        val chooser = StorageChooser.Builder()
                .withActivity(this@MainActivity)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build()

        chooser.setOnSelectListener { path ->
            preferencesHelper.setOutputFolder(path)
        }
        chooser.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_WRITE_STORAGE_ONLY -> if (!verifyPermissions(*grantResults)) {
                retryPermissionsRequest(permissions, requestCode)
            }
            REQUEST_WRITE_STORAGE_AND_SHOW_STORAGE -> if (verifyPermissions(*grantResults)) {
                showStorageChooser()
            } else {
                retryPermissionsRequest(permissions, requestCode)
            }
        }
    }

    private fun retryPermissionsRequest(permissions: Array<out String>, requestCode: Int) {
        Snackbar.make(content, "Permission needed to download files", Snackbar.LENGTH_LONG)
                .setAction("RETRY") {
                    ActivityCompat.requestPermissions(this, permissions, requestCode)
                }
                .show()
    }

    override fun onNavigateUp() = navController.navigateUp()

    private fun verifyPermissions(vararg grantResults: Int): Boolean {
        return grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }

    companion object {
        const val REQUEST_WRITE_STORAGE_ONLY = 0
        const val REQUEST_WRITE_STORAGE_AND_SHOW_STORAGE = 1
    }
}