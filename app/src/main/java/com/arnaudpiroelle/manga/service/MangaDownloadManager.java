package com.arnaudpiroelle.manga.service;

import android.os.Environment;
import android.util.Log;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MangaDownloadManager {

    private MangaDownloaderCallback callback;
    private ProviderRegistry providerRegistry;

    public MangaDownloadManager(MangaDownloaderCallback callback, ProviderRegistry providerRegistry) {
        this.callback = callback;
        this.providerRegistry = providerRegistry;
    }

    public void startDownload(List<Manga> mangas) {
        Observable.from(mangas)
                .subscribeOn(Schedulers.io())
                .filter(this::hasValidProvider)
                .doOnNext(this::onMangaDownload)
                .doOnCompleted(this::onAllMangasDownloaded)
                .doOnError(this::onError)
                .flatMap(this::downloadManga)
                .subscribe();

    }

    private Observable<?> downloadManga(Manga manga) {
        MangaProvider mangaProvider = providerRegistry.get(manga.getProvider());

        List<Chapter> chapters = mangaProvider.findChaptersFor(manga);
        manga.setChapters(chapters);

        return Observable.from(chapters)
                .skip(toBeSkipped(manga))
                .doOnNext(this::onChapterDownload)
                .doOnCompleted(this::onAllChaptersDownloaded)
                .doOnError(this::onError)
                .flatMap(chapter -> downloadChapter(manga, chapter));
    }

    private Observable<?> downloadChapter(Manga manga, Chapter chapter) {
        MangaProvider mangaProvider = providerRegistry.get(manga.getProvider());

        List<Page> pages = mangaProvider.findPagesFor(chapter);
        chapter.setPages(pages);

        return Observable.from(pages)
                .doOnNext(page -> onPageDownload(manga, chapter, page))
                .doOnCompleted(() -> onAllPagesDownloaded(manga, chapter))
                .doOnError(this::onError)
                .flatMap(page -> downloadPage(manga, chapter, page));
    }

    private void onError(Throwable throwable) {

    }

    private Observable<?> downloadPage(Manga manga, Chapter chapter, Page page) {
        return Observable.create(subscriber -> {
            InputStream inputStream = providerRegistry.get(manga.getProvider()).findPage(page);

            File pageFile = getPageFile(manga, chapter, page);

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

    private Boolean hasValidProvider(Manga manga) {
        return providerRegistry.get(manga.getProvider()) != null;
    }

    private int toBeSkipped(Manga manga) {
        if (manga.getLastChapter() == null || manga.getLastChapter().isEmpty()) {
            return manga.getChapters().size() - 1;
        } else {
            if ("all".equals(manga.getLastChapter())){
                return 0;
            } else {
                for (int i = 0; i < manga.getChapters().size(); i++) {
                    if ( manga.getChapters().get(i).getChapterNumber().equals(manga.getLastChapter())){
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

    private void onAllChaptersDownloaded() {
        Log.i("NewMangaDownloader", "All chapters downloaded");
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

    private File getEbooksFolder() {
        return new File(Environment.getExternalStorageDirectory(), "eBooks");
    }

    private File getMangaFolder(Manga manga) {
        return new File(getEbooksFolder(), manga.getName());
    }

    private File getChapterFolder(Manga manga, Chapter chapter) {
        return new File(getMangaFolder(manga), chapter.getChapterNumber());
    }

    private File getPageFile(Manga manga, Chapter chapter, Page page) {
        String pageFormated = String.format("%03d.%s", Integer.valueOf(page.getPageNumber()), page.getExtension());

        File chapterFolder = getChapterFolder(manga, chapter);
        chapterFolder.mkdirs();

        return new File(chapterFolder + "/" + pageFormated);
    }

    public void zipChapter(Manga manga, Chapter chapter) {

        Observable.create((subscriber -> {
            File mangaFolder = getMangaFolder(manga);

            File zipFile = new File(String.format("%s/%s - %s.cbz",
                    mangaFolder.getAbsoluteFile(),
                    manga.getName(),
                    chapter.getChapterNumber()));

            try (FileOutputStream dest = new FileOutputStream(zipFile);
                 ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {

                zipFile.createNewFile();
                File chapterFolder = getChapterFolder(manga, chapter);

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
            }
        }))
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
