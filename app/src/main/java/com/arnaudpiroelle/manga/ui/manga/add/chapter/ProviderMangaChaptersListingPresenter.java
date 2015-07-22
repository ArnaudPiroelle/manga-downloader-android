package com.arnaudpiroelle.manga.ui.manga.add.chapter;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.arnaudpiroelle.manga.model.Chapter.ChapterBuilder.createChapter;

public class ProviderMangaChaptersListingPresenter implements Presenter<Chapter> {

    private final ProviderMangaListingPresenterCallback callback;
    private final MangaProvider provider;
    private final Manga manga;

    public ProviderMangaChaptersListingPresenter(
            ProviderMangaListingPresenterCallback callback,
            MangaProvider provider,
            Manga manga) {

        this.callback = callback;
        this.provider = provider;
        this.manga = manga;
    }

    @Override
    public void list() {
        Observable.<List<Chapter>>create(subscriber -> {
            callback.onListingLoading();

            List<Chapter> chapters = new ArrayList<>();
            chapters.add(createChapter().withChapterNumber("all").build());
            chapters.addAll(provider.findChapters(manga));

            subscriber.onNext(chapters);

            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onListingLoaded);
    }

    public interface ProviderMangaListingPresenterCallback {
        void onListingLoading();

        void onListingLoaded(List<Chapter> chapters);
    }
}
