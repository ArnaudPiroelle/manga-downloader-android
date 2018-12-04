package com.arnaudpiroelle.manga.worker

import androidx.work.WorkManager
import com.arnaudpiroelle.manga.worker.utils.FileHelper
import com.arnaudpiroelle.manga.worker.utils.PreferencesHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

val workerModule = module {
    single { WorkManager.getInstance() }
    single { PreferencesHelper(androidContext(), get()) }
    single { FileHelper(get()) }
    single { TaskManager(get()) }
}