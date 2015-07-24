package com.arnaudpiroelle.manga.core.utils;

import android.os.Environment;

import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.model.Page;

import java.io.File;

import javax.inject.Inject;

public class FileHelper {

    @Inject
    public FileHelper() {

    }

    public File getEbooksFolder() {
        return new File(Environment.getExternalStorageDirectory(), "eBooks");
    }

    public File getMangaFolder(Manga manga) {
        return new File(getEbooksFolder(), manga.getName());
    }

    public File getChapterFolder(Manga manga, Chapter chapter) {
        return new File(getMangaFolder(manga), chapter.getChapterNumber());
    }

    public File getPageFile(Manga manga, Chapter chapter, Page page) {
        String pageFormated = String.format("%03d.%s", Integer.valueOf(page.getPageNumber()), page.getExtension());

        File chapterFolder = getChapterFolder(manga, chapter);
        chapterFolder.mkdirs();

        return new File(chapterFolder + "/" + pageFormated);
    }
}
