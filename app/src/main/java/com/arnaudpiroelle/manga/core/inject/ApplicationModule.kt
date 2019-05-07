package com.arnaudpiroelle.manga.core.inject

import android.preference.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val applicationModule = module(createdAtStart = true) {
    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
}