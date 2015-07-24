package com.arnaudpiroelle.manga.service;

import android.os.Environment;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
import com.arnaudpiroelle.manga.core.utils.FileHelper;
import com.arnaudpiroelle.manga.core.utils.HttpUtils;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.model.Page;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import rx.Observable;
import rx.schedulers.Schedulers;

public class MangaDownloadManager {

    private MangaDownloaderCallback callback;
    private ProviderRegistry providerRegistry;
    private FileHelper fileHelper;

    public MangaDownloadManager(MangaDownloaderCallback callback, ProviderRegistry providerRegistry, FileHelper fileHelper) {
        this.callback = callback;
        this.providerRegistry = providerRegistry;
        this.fileHelper = fileHelper;
    }

    public void startDownload(List<Manga> mangas) {
        getValidMangas(mangas)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::onMangaDownload)
                .doOnCompleted(this::onAllMangasDownloaded)
                .doOnError(this::onError)
                .flatMap(this::downloadManga)
                .subscribe();

    }

    @VisibleForTesting Observable<Manga> getValidMangas(List<Manga> mangas){
        return Observable.from(mangas)
                .filter(this::hasValidProvider);
    }

    @VisibleForTesting Observable<Chapter> getNewChapters(Manga manga){
        MangaProvider mangaProvider = providerRegistry.get(manga.getProvider());

        List<Chapter> chapters = mangaProvider.findChapters(manga);
        manga.setChapters(chapters);

        return Observable.from(chapters)
                .skip(alreadyDownloadedChapters(manga));
    }

    @VisibleForTesting Observable<Page> getPages(Manga manga, Chapter chapter){
        MangaProvider mangaProvider = providerRegistry.get(manga.getProvider());

        List<Page> pages = mangaProvider.findPages(chapter);
        chapter.setPages(pages);

        return Observable.from(pages)
                .doOnNext(page -> onPageDownload(manga, chapter, page));
    }

    private Observable<?> downloadManga(final Manga manga) {
        return getNewChapters(manga)
                .doOnNext(this::onChapterDownload)
                .doOnCompleted(() -> onAllChaptersDownloaded(manga))
                .doOnError(this::onError)
                .flatMap(chapter -> downloadChapter(manga, chapter));
    }

    private Observable<?> downloadChapter(final Manga manga, final Chapter chapter) {
        return getPages(manga, chapter)
                .doOnNext(page -> onPageDownload(manga, chapter, page))
                .doOnCompleted(() -> onAllPagesDownloaded(manga, chapter))
                .doOnError(this::onError)
                .flatMap(page -> downloadPage(manga, chapter, page));
    }

    private Observable<File> downloadPage(final Manga manga, final Chapter chapter, final Page page) {
        return Observable.<File>create(subscriber -> {
            InputStream inputStream = providerRegistry.get(manga.getProvider()).findPage(page);

            File pageFile = fileHelper.getPageFile(manga, chapter, page);

            try {
                pageFile.createNewFile();
                HttpUtils.writeFile(inputStream, pageFile);
            } catch (IOException e) {
                Log.e("DownloadService", "Error", e);
            }

            subscriber.onNext(pageFile);
            subscriber.onCompleted();
        }).doOnError(this::onError);
    }

    private void onError(Throwable throwable) {
        callback.onDownloadError(throwable);
    }

    @VisibleForTesting Boolean hasValidProvider(Manga manga) {
        return providerRegistry.get(manga.getProvider()) != null;
    }

    @VisibleForTesting int alreadyDownloadedChapters(Manga manga) {
        if (manga.getLastChapter() == null || manga.getLastChapter().isEmpty()) {
            return manga.getChapters().size() - 1;
        } else {
            if ("all".equals(manga.getLastChapter())) {
                return 0;
            } else {
                for (int i = 0; i < manga.getChapters().size(); i++) {
                    if (manga.getChapters().get(i).getChapterNumber().equals(manga.getLastChapter())) {
                        return i + 1;
                    }
                }
            }
        }

        return 0;
    }

    private void onAllMangasDownloaded() {
        Log.i("NewMangaDownloader", "All mangas downloaded");
        callback.onDownloadCompleted();
    }

    private void onMangaDownload(Manga manga) {
        Log.i("NewMangaDownloader", "Manga " + manga.getName() + " download");
    }

    private void onAllChaptersDownloaded(Manga manga) {
        Log.i("NewMangaDownloader", "All chapters downloaded");
        callback.onCompleteManga(manga);
    }

    private void onChapterDownload(Chapter chapter) {
        Log.i("NewMangaDownloader", "Chapter " + chapter.getChapterNumber() + " download");
    }

    private void onAllPagesDownloaded(Manga manga, Chapter chapter) {
        Log.i("NewMangaDownloader", "All pages downloaded");
        callback.onCompleteChapter(manga, chapter);
    }

    private void onPageDownload(Manga manga, Chapter chapter, Page page) {
        Log.i("NewMangaDownloader", "Page " + page.getPageNumber() + "/" + chapter.getPages().size() + " download");
        callback.onCompletePage(manga, chapter, page);
    }

    public void zipChapter(final Manga manga, final Chapter chapter) {

        Observable.create(subscriber -> {
            File mangaFolder = fileHelper.getMangaFolder(manga);

            File zipFile = new File(String.format("%s/%s - %s.cbz",
                    mangaFolder.getAbsoluteFile(),
                    manga.getName(),
                    chapter.getChapterNumber()));

            try (FileOutputStream dest = new FileOutputStream(zipFile);
                 ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {

                zipFile.createNewFile();
                File chapterFolder = fileHelper.getChapterFolder(manga, chapter);

                int buffer = 1024;
                byte data[] = new byte[buffer];

                for (File file : chapterFolder.listFiles()) {
                    try (FileInputStream fi = new FileInputStream(file);
                         BufferedInputStream origin = new BufferedInputStream(fi, buffer)) {

                        ZipEntry entry = new ZipEntry(file.getName());
                        out.putNextEntry(entry);

                        int count;
                        while ((count = origin.read(data, 0, buffer)) != -1) {
                            out.write(data, 0, count);
                        }
                    }
                }

                for (File file : chapterFolder.listFiles()) {
                    file.delete();
                }

                chapterFolder.delete();

            } catch (IOException e) {
                Log.e("MangaDownloadManager", "Error when zip chapter");

                if (zipFile.exists()) {
                    zipFile.delete();
                }
            } finally {
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe();

    }

    public interface MangaDownloaderCallback {

        void onDownloadError(Throwable throwable);

        void onDownloadCompleted();

        void onCompleteManga(Manga manga);

        void onCompleteChapter(Manga manga, Chapter chapter);

        void onCompletePage(Manga manga, Chapter chapter, Page page);
    }
}
