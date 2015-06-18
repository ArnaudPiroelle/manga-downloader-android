package com.arnaudpiroelle.manga.event;

public class ProviderSelectedEvent {
    public String provider;

    public ProviderSelectedEvent(String name) {
        provider = name;
    }
}
