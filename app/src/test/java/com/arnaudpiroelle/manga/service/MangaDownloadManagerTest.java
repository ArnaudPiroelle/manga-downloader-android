package com.arnaudpiroelle.manga.service;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.arnaudpiroelle.manga.model.Chapter.ChapterBuilder.createChapter;
import static com.arnaudpiroelle.manga.model.Manga.MangaBuilder.createManga;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MangaDownloadManagerTest {

    @Mock
    ProviderRegistry providerRegistry;

    @InjectMocks MangaDownloadManager mangaDownloadManager;

    @Test
    public void should_return_false_if_manga_has_an_invalid_provider() throws Exception {
        // Given
        Manga manga = createManga().withName("manga1").withProvider("provider2").build();

        MangaProvider provider = Mockito.mock(MangaProvider.class);
        when(providerRegistry.get("provider1")).thenReturn(provider);

        // When
        Boolean result = mangaDownloadManager.hasValidProvider(manga);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_return_true_if_manga_has_a_valid_provider() throws Exception {
        // Given
        Manga manga = createManga().withName("manga1").withProvider("provider1").build();

        MangaProvider provider = Mockito.mock(MangaProvider.class);
        when(providerRegistry.get("provider1")).thenReturn(provider);

        // When
        Boolean result = mangaDownloadManager.hasValidProvider(manga);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_return_last_chapter_if_manga_has_empty_last_chapter() throws Exception {
        // Given
        Manga manga = createManga().withLastChapter(null).build();
        Chapter chapter1 = createChapter().withChapterNumber("1").build();
        Chapter chapter2 = createChapter().withChapterNumber("2").build();
        manga.setChapters(Lists.newArrayList(chapter1, chapter2));

        // When
        int result = mangaDownloadManager.alreadyDownloadedChapters(manga);

        // Then
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void should_return_0_if_manga_has_all_value_for_last_chapter() throws Exception {
        // Given
        Manga manga = createManga().withLastChapter("all").build();

        // When
        int result = mangaDownloadManager.alreadyDownloadedChapters(manga);

        // Then
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void should_return_correct_position_if_manga_has_a_last_chapter() throws Exception {
        // Given
        Manga manga = createManga().withLastChapter("3").build();
        Chapter chapter1 = createChapter().withChapterNumber("1").build();
        Chapter chapter2 = createChapter().withChapterNumber("2").build();
        Chapter chapter3 = createChapter().withChapterNumber("3").build();
        Chapter chapter4 = createChapter().withChapterNumber("4").build();
        manga.setChapters(Lists.newArrayList(chapter1, chapter2, chapter3, chapter4));

        // When
        int result = mangaDownloadManager.alreadyDownloadedChapters(manga);

        // Then
        assertThat(result).isEqualTo(3);
    }

    @Test
    public void should_filter_invalid_mangas() {
        // Given
        Manga manga1 = createManga().withName("manga1").withProvider("provider1").build();
        Manga manga2 = createManga().withName("manga2").withProvider("provider2").build();
        List<Manga> mangas = Lists.newArrayList(manga1, manga2);

        MangaProvider provider = Mockito.mock(MangaProvider.class);
        when(providerRegistry.get("provider1")).thenReturn(provider);

        // When
        List<Manga> mangasFiltered = mangaDownloadManager.getValidMangas(mangas)
                .toList()
                .toBlocking()
                .single();

        // Then
        assertThat(mangasFiltered.size()).isEqualTo(1);
        assertThat(mangasFiltered.get(0)).isEqualTo(manga1);
    }

    @Test
    public void should_return_all_chapters_of_manga() throws Exception {
        // Given
        Manga manga = createManga().withName("manga1").withLastChapter("all").withProvider("provider1").build();

        Chapter chapter1 = createChapter().withName("Chapter 1").withMangaAlias("manga1").withChapterNumber("01").build();
        Chapter chapter2 = createChapter().withName("Chapter 2").withMangaAlias("manga1").withChapterNumber("02").build();

        MangaProvider provider = Mockito.mock(MangaProvider.class);
        when(providerRegistry.get("provider1")).thenReturn(provider);
        when(provider.findChapters(manga)).thenReturn(Lists.newArrayList(chapter1, chapter2));

        // When
        List<Chapter> chapters = mangaDownloadManager.getNewChapters(manga)
                .toList()
                .toBlocking()
                .single();

        // Then
        assertThat(manga.getChapters()).containsAll(chapters);
        assertThat(chapters.size()).isEqualTo(2);

    }

    @Test
    public void should_return_last_chapters_of_manga() throws Exception {
        // Given
        Manga manga = createManga().withName("manga1").withLastChapter("").withProvider("provider1").build();

        Chapter chapter1 = createChapter().withName("Chapter 1").withMangaAlias("manga1").withChapterNumber("01").build();
        Chapter chapter2 = createChapter().withName("Chapter 2").withMangaAlias("manga1").withChapterNumber("02").build();

        MangaProvider provider = Mockito.mock(MangaProvider.class);
        when(providerRegistry.get("provider1")).thenReturn(provider);
        when(provider.findChapters(manga)).thenReturn(Lists.newArrayList(chapter1, chapter2));

        // When
        List<Chapter> chapters = mangaDownloadManager.getNewChapters(manga)
                .toList()
                .toBlocking()
                .single();

        // Then
        assertThat(manga.getChapters()).containsAll(chapters);
        assertThat(chapters.size()).isEqualTo(1);
        assertThat(chapters.get(0)).isEqualTo(chapter2);

    }

    @Test
    public void should_return_only_new_chapters_of_manga() throws Exception {
        // Given
        Manga manga = createManga().withName("manga1").withLastChapter("01").withProvider("provider1").build();

        Chapter chapter1 = createChapter().withName("Chapter 1").withMangaAlias("manga1").withChapterNumber("01").build();
        Chapter chapter2 = createChapter().withName("Chapter 2").withMangaAlias("manga1").withChapterNumber("02").build();
        Chapter chapter3 = createChapter().withName("Chapter 3").withMangaAlias("manga1").withChapterNumber("03").build();

        MangaProvider provider = Mockito.mock(MangaProvider.class);
        when(providerRegistry.get("provider1")).thenReturn(provider);
        when(provider.findChapters(manga)).thenReturn(Lists.newArrayList(chapter1, chapter2, chapter3));

        // When
        List<Chapter> chapters = mangaDownloadManager.getNewChapters(manga)
                .toList()
                .toBlocking()
                .single();

        // Then
        assertThat(manga.getChapters()).containsAll(chapters);
        assertThat(chapters.size()).isEqualTo(2);
        assertThat(chapters).containsAll(Lists.newArrayList(chapter2, chapter3));

    }
}