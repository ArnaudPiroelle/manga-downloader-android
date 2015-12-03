package com.arnaudpiroelle.manga.core.inject

import com.arnaudpiroelle.manga.core.inject.module.ApplicationModule
import com.arnaudpiroelle.manga.provider.japscan.JapScanModule
import com.arnaudpiroelle.manga.provider.mangapanda.MangaPandaModule
import com.arnaudpiroelle.manga.service.DownloadService
import com.arnaudpiroelle.manga.ui.history.HistoryFragment
import com.arnaudpiroelle.manga.ui.manga.NavigationActivity
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaActivity
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaFragment
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingFragment
import com.arnaudpiroelle.manga.ui.manga.list.view.MangaView
import com.arnaudpiroelle.manga.ui.manga.modify.ModifyMangaDialogFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class, JapScanModule::class, MangaPandaModule::class))
interface MangaComponent {

    fun inject(navigationActivity: NavigationActivity)
    fun inject(addMangaActivity: AddMangaActivity)

    fun inject(modifyMangaDialogFragment: ModifyMangaDialogFragment)
    fun inject(mangaListingFragment: MangaListingFragment)
    fun inject(historyFragment: HistoryFragment)
    fun inject(addMangaFragment: AddMangaFragment)

    fun inject(mangaView: MangaView)

    fun inject(downloadService: DownloadService)
}
