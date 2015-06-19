package com.arnaudpiroelle.manga.core.inject;

import com.arnaudpiroelle.manga.core.inject.module.AndroidModule;
import com.arnaudpiroelle.manga.core.inject.module.ApplicationModule;
import com.arnaudpiroelle.manga.provider.japscan.JapScanModule;
import com.arnaudpiroelle.manga.service.DownloadService;
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaActivity;
import com.arnaudpiroelle.manga.ui.manga.add.manga.ProviderMangaListingFragment;
import com.arnaudpiroelle.manga.ui.manga.add.provider.ProviderListingFragment;
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingActivity;
import com.arnaudpiroelle.manga.ui.manga.list.MangaView;
import com.arnaudpiroelle.manga.ui.manga.modify.ModifyMangaDialogFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        AndroidModule.class,
        JapScanModule.class
})
public interface MangaComponent {

        void inject(DownloadService downloadService);

        void inject(MangaListingActivity mangaListingActivity);
        void inject(AddMangaActivity addMangaActivity);

        void inject(ProviderListingFragment providerListingFragment);
        void inject(ProviderMangaListingFragment providerMangaListingFragment);

        void inject(MangaView mangaView);

        void inject(ModifyMangaDialogFragment modifyMangaDialogFragment);
}
