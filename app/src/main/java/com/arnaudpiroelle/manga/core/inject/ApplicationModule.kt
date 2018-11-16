package com.arnaudpiroelle.manga.core.inject

import android.preference.PreferenceManager
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.manager.Schedulers
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.service.task.DownloadChapterTaskExecution
import com.arnaudpiroelle.manga.service.task.RetrieveChaptersTaskExecution
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val applicationModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single { PreferencesHelper(androidContext(), get()) }
    single { RetrieveChaptersTaskExecution(get(), get(), get(), get()) }
    single { DownloadChapterTaskExecution(get(), get(), get(), get(), get(), get()) }
    single { ProviderRegistry }
    single { Schedulers(get(), get()) }
}