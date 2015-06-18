package com.arnaudpiroelle.manga.core.ui.presenter;

public interface Presenter<T> {
    void list();

    void filter(String query);
}
