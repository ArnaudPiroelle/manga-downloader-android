package com.arnaudpiroelle.manga.core.utils;

import android.os.Environment;

import com.arnaudpiroelle.manga.BuildConfig;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.model.Page;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;

import static com.arnaudpiroelle.manga.model.Chapter.ChapterBuilder.createChapter;
import static com.arnaudpiroelle.manga.model.Manga.MangaBuilder.createManga;
import static com.arnaudpiroelle.manga.model.Page.PageBuilder.createPage;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class FileHelperTest {

    private FileHelper fileHelper;

    @Before
    public void setUp() throws Exception {
        fileHelper = new FileHelper();
    }

    @Test
    public void should_compute_ebooks_folder() {
        // Given / When
        File ebooksFolder = fileHelper.getEbooksFolder();

        // Then
        assertThat(ebooksFolder.getAbsolutePath()).isEqualTo(Environment.getExternalStorageDirectory() + "/eBooks");

    }

    @Test
    public void should_compute_manga_folder() {
        // Given
        Manga manga = Companion.createManga().withName("My Manga").build();

        // When
        File ebooksFolder = fileHelper.getMangaFolder(manga);

        // Then
        assertThat(ebooksFolder.getAbsolutePath()).isEqualTo(Environment.getExternalStorageDirectory() + "/eBooks/My Manga");

    }

    @Test
    public void should_compute_chapter_folder() {
        // Given
        Manga manga = Companion.createManga().withName("My Manga").build();
        Chapter chapter = Companion.createChapter().withChapterNumber("01").build();

        // When
        File ebooksFolder = fileHelper.getChapterFolder(manga, chapter);

        // Then
        assertThat(ebooksFolder.getAbsolutePath()).isEqualTo(Environment.getExternalStorageDirectory() + "/eBooks/My Manga/01");

    }

    @Test
    public void should_compute_page_file() {
        // Given
        Manga manga = Companion.createManga().withName("My Manga").build();
        Chapter chapter = Companion.createChapter().withChapterNumber("01").build();
        Page page = Companion.createPage().withExtenstion("jpg").withPageNumber("1").build();

        // When
        File ebooksFolder = fileHelper.getPageFile(manga, chapter, page);

        // Then
        assertThat(ebooksFolder.getAbsolutePath()).isEqualTo(Environment.getExternalStorageDirectory() + "/eBooks/My Manga/01/001.jpg");

    }
}