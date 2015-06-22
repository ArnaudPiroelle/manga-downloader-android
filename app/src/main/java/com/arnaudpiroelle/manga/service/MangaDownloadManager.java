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
import rx.Observable.OnSubscribe;
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
        OnSubscribe<Manga> mangaOnSubscribe = subscriber -> {
            for (Manga manga : mangas) {
                downloadManga(manga);
                subscriber.onNext(manga);
            }
            subscriber.onCompleted();
        };

        Observable.create(mangaOnSubscribe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        callback::onCompleteManga,
                        callback::onDownloadError,
                        callback::onDownloadCompleted
                );
    }

    private void downloadManga(Manga manga) {
        MangaProvider provider = providerRegistry.get(manga.getProvider());
        if (provider != null) {

            Log.i("Downloaded manga", manga.getName());

            boolean hasLastChapter = (manga.getLastChapter() != null &&
                    !manga.getLastChapter().isEmpty());

            boolean isFullDownload = hasLastChapter && "all".equals(manga.getLastChapter());

            List<Chapter> chapters = provider.findChaptersFor(manga);
            manga.setChapters(chapters);

            int skip = 0;
            if (hasLastChapter) {
                if (isFullDownload) {
                    skip = -1;
                } else {
                    while (skip < chapters.size() &&
                            !chapters.get(skip).getChapterNumber().equals(manga.getLastChapter())) {
                        skip++;
                    }
                }
            } else {
                skip = chapters.size() - 2;
            }


            for (int i = skip + 1; i < chapters.size(); i++) {
                Chapter chapter = chapters.get(i);
                downloadChapter(manga, chapter);
            }
        }
    }

    private void downloadChapter(Manga manga, Chapter chapter) {
        Log.i("Downloaded chapter", chapter.getChapterNumber());
        List<Page> pages = providerRegistry.get(manga.getProvider()).findPagesFor(chapter);

        chapter.setPages(pages);

        for (Page page : pages) {
            downloadPage(manga, chapter, page);
        }

        callback.onCompleteChapter(manga, chapter);
    }

    private void downloadPage(Manga manga, Chapter chapter, Page page) {
        Log.i("DownloadService", page.getPageNumber() + "/" + chapter.getPages().size());
        InputStream inputStream = providerRegistry.get(manga.getProvider()).findPage(page);

        File pageFile = getPageFile(manga, chapter, page);

        if (!pageFile.exists()) {
            try {
                pageFile.createNewFile();
                HttpUtils.writeFile(inputStream, pageFile);
            } catch (IOException e) {
                Log.e("DownloadService", "Error", e);
            }
        }

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
                .observeOn(AndroidSchedulers.mainThread())
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
