package com.arnaudpiroelle.manga.core.provider;

import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.model.Page;

import java.io.InputStream;
import java.util.List;

public interface MangaProvider {

    List<Manga> findMangas();

    List<Chapter> findChaptersFor(Manga manga);

    List<Page> findPagesFor(Chapter chapter);

    InputStream findPage(Page page);

    String getName();
}
