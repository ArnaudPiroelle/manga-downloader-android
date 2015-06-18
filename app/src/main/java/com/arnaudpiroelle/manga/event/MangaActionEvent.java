package com.arnaudpiroelle.manga.event;

import com.arnaudpiroelle.manga.model.Manga;

public class MangaActionEvent {
    public Manga manga;
    public ActionType type;

    public MangaActionEvent(Manga manga, ActionType type) {
        this.manga = manga;
        this.type = type;
    }

    public enum ActionType {
        MODIFY,
        REMOVE
    }
}
