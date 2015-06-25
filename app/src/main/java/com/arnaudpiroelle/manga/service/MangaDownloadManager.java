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
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
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
                .filter(new Func1<Manga, Boolean>() {
                    @Override
                    public Boolean call(Manga manga) {
                        return hasValidProvider(manga);
                    }
                })
                .doOnNext(new Action1<Manga>() {
                    @Override
                    public void call(Manga manga) {
                        onMangaDownload(manga);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        onAllMangasDownloaded();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onError(throwable);
                    }
                })
                .flatMap(new Func1<Manga, Observable<?>>() {
                    @Override
                    public Observable<?> call(Manga manga) {
                        return downloadManga(manga);
                    }
                })
                .subscribe();

    }

    private Observable<?> downloadManga(final Manga manga) {
        MangaProvider mangaProvider = providerRegistry.get(manga.getProvider());

        List<Chapter> chapters = mangaProvider.findChapters(manga);
        manga.setChapters(chapters);

        return Observable.from(chapters)
                .skip(toBeSkipped(manga))
                .doOnNext(new Action1<Chapter>() {
                    @Override
                    public void call(Chapter chapter) {
                        onChapterDownload(chapter);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        onAllChaptersDownloaded(manga);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onError(throwable);
                    }
                })
                .flatMap(new Func1<Chapter, Observable<?>>() {
                    @Override
                    public Observable<?> call(Chapter chapter) {
                        return downloadChapter(manga, chapter);
                    }
                });
    }

    private Observable<?> downloadChapter(final Manga manga, final Chapter chapter) {
        MangaProvider mangaProvider = providerRegistry.get(manga.getProvider());

        List<Page> pages = mangaProvider.findPages(chapter);
        chapter.setPages(pages);

        return Observable.from(pages)
                .doOnNext(new Action1<Page>() {
                    @Override
                    public void call(Page page) {
                        onPageDownload(manga, chapter, page);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        onAllPagesDownloaded(manga, chapter);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onError(throwable);
                    }
                })
                .flatMap(new Func1<Page, Observable<?>>() {
                    @Override
                    public Observable<?> call(Page page) {
                        return downloadPage(manga, chapter, page);
                    }
                });
    }

    private Observable<File> downloadPage(final Manga manga, final Chapter chapter, final Page page) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
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
            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                onError(throwable);
            }
        });
    }

    private void onError(Throwable throwable) {
        callback.onDownloadError(throwable);
    }

    private Boolean hasValidProvider(Manga manga) {
        return providerRegistry.get(manga.getProvider()) != null;
    }

    private int toBeSkipped(Manga manga) {
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

    public void zipChapter(final Manga manga, final Chapter chapter) {

        Observable.create(new Observable.OnSubscribe<Object>() {
                              @Override
                              public void call(Subscriber<? super Object> subscriber) {
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
                                  } finally {
                                      subscriber.onCompleted();
                                  }
                              }
                          }
        )
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
