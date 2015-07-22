package com.arnaudpiroelle.manga.event;

import com.arnaudpiroelle.manga.model.Manga;

public class ChapterSelectedEvent {
    public Manga manga;

    public ChapterSelectedEvent(Manga manga) {
        this.manga = manga;
    }
}
