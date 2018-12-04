package com.arnaudpiroelle.manga.core.inject

import android.preference.PreferenceManager
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val applicationModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single { ProviderRegistry }
}