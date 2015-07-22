package com.arnaudpiroelle.manga.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MangaTest {

    @Test
    public void should_compare_two_manga() throws Exception {
        Manga manga1 = Manga.MangaBuilder.createManga()
                .withName("bbb")
                .build();
        Manga manga2 = Manga.MangaBuilder.createManga()
                .withName("aaa")
                .build();

        int compareTo = manga2.compareTo(manga1);

        assertThat(compareTo).isEqualTo(-1);

    }
}