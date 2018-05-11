package com.arnaudpiroelle.manga.core.inject

import com.arnaudpiroelle.manga.core.inject.module.ApplicationModule
import com.arnaudpiroelle.manga.core.inject.module.DatabaseModule
import com.arnaudpiroelle.manga.core.inject.module.JapScanModule
import com.arnaudpiroelle.manga.service.DownloadService
import com.arnaudpiroelle.manga.ui.explorer.ExplorerActivity
import com.arnaudpiroelle.manga.ui.history.HistoryFragment
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaActivity
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingFragment
import com.arnaudpiroelle.manga.ui.manga.modify.ModifyMangaDialogFragment
import com.arnaudpiroelle.manga.ui.settings.SettingsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(ApplicationModule::class), (DatabaseModule::class), (JapScanModule::class)])
interface MangaComponent {

    fun inject(explorerActivity: ExplorerActivity)
    fun inject(addMangaActivity: AddMangaActivity)

    fun inject(modifyMangaDialogFragment: ModifyMangaDialogFragment)
    fun inject(mangaListingFragment: MangaListingFragment)

    fun inject(historyFragment: HistoryFragment)
    fun inject(downloadService: DownloadService)
    fun inject(settingsFragment: SettingsFragment)
}
