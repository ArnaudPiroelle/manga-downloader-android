package com.arnaudpiroelle.manga.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import com.arnaudpiroelle.manga.R


class MainActivity : AppCompatActivity() {
    private val navController by lazy { findNavController(this, R.id.nav_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    override fun onNavigateUp() = navController.navigateUp()

}