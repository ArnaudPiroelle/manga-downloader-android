package com.arnaudpiroelle.manga.model;

import android.os.Parcel;

import com.arnaudpiroelle.manga.BuildConfig;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.arnaudpiroelle.manga.model.Chapter.ChapterBuilder.createChapter;
import static com.arnaudpiroelle.manga.model.Manga.MangaBuilder.createManga;
import static com.arnaudpiroelle.manga.model.Page.PageBuilder.createPage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MangaTest {

    @Test
    public void should_compare_two_manga() throws Exception {
        Manga manga1 = Companion.createManga()
                .withName("bbb")
                .build();
        Manga manga2 = Companion.createManga()
                .withName("aaa")
                .build();

        int compareTo = manga2.compareTo(manga1);

        assertThat(compareTo).isEqualTo(-1);

    }

    @Test
    public void should_restore_manga_from_parcel() throws Exception {
        // Given
        Manga manga = Companion.createManga()
                .withId(1)
                .withName("Title")
                .withMangaAlias("Alias")
                .withProvider("Provider")
                .withLastChapter("Last Chapter")
                .build();

        Chapter chapter = Companion.createChapter()
                .withName("Chapter")
                .withMangaAlias("Alias")
                .withChapterNumber("01")
                .build();

        Page page = Companion.createPage()
                .withName("01")
                .withChapterNumber("01")
                .withMangaAlias("Alias")
                .withPageNumber("01")
                .withExtenstion("jpg")
                .build();

        chapter.setPages(newArrayList(page));
        manga.setChapters(newArrayList(chapter));

        // When
        Parcel parcel = Parcel.obtain();
        manga.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        // Then
        Manga mangaFromParcel = Manga.CREATOR.createFromParcel(parcel);

        assertThat(mangaFromParcel.getId()).isEqualTo(1);
        assertThat(mangaFromParcel.getName()).isEqualTo("Title");
        assertThat(mangaFromParcel.getMangaAlias()).isEqualTo("Alias");
        assertThat(mangaFromParcel.getProvider()).isEqualTo("Provider");
        assertThat(mangaFromParcel.getLastChapter()).isEqualTo("Last Chapter");
        assertThat(mangaFromParcel.getChapters().size()).isEqualTo(1);

        Chapter chapterFromParcel = mangaFromParcel.getChapters().get(0);
        assertThat(chapterFromParcel.getName()).isEqualTo("Chapter");
        assertThat(chapterFromParcel.getMangaAlias()).isEqualTo("Alias");
        assertThat(chapterFromParcel.getChapterNumber()).isEqualTo("01");
        assertThat(chapterFromParcel.getPages().size()).isEqualTo(1);

        Page pageFromParcel = chapterFromParcel.getPages().get(0);
        assertThat(pageFromParcel.getPageName()).isEqualTo("01");
        assertThat(pageFromParcel.getChapterNumber()).isEqualTo("01");
        assertThat(pageFromParcel.getMangaAlias()).isEqualTo("Alias");
        assertThat(pageFromParcel.getPageNumber()).isEqualTo("01");
        assertThat(pageFromParcel.getExtension()).isEqualTo("jpg");

    }
}