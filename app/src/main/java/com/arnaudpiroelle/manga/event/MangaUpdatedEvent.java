package com.arnaudpiroelle.manga.event;

import com.arnaudpiroelle.manga.model.Manga;

public class MangaUpdatedEvent {
    public Manga manga;

    public MangaUpdatedEvent(Manga manga) {

        this.manga = manga;
    }
}
