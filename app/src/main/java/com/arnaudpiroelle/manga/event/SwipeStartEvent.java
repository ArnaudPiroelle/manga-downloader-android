package com.arnaudpiroelle.manga.event;

import com.arnaudpiroelle.manga.model.Manga;

public class SwipeStartEvent {
    public Manga manga;

    public SwipeStartEvent(Manga manga) {
        this.manga = manga;
    }
}
