package com.arnaudpiroelle.manga.event;

import com.arnaudpiroelle.manga.model.Manga;

public class MangaSelectedEvent {
    public Manga manga;

    public MangaSelectedEvent(Manga manga) {
        this.manga = manga;
    }
}
