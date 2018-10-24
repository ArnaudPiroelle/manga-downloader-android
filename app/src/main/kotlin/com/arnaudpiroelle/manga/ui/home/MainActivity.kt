package com.arnaudpiroelle.manga.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.arnaudpiroelle.manga.R


class MainActivity : AppCompatActivity() {
    private val navController by lazy { Navigation.findNavController(this, R.id.nav_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}