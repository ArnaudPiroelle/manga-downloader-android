package com.arnaudpiroelle.manga.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.AutoIncrement;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Mangas")
public class Manga extends Model implements Comparable<Manga>, Parcelable{

    @Key
    @AutoIncrement
    @Column("id")
    private long id;

    @Column("name")
    private String name;

    @Column("mangaAlias")
    private String mangaAlias;

    @Column("provider")
    private String provider;

    @Column("lastChapter")
    private String lastChapter;

    private List<Chapter> chapters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMangaAlias() {
        return mangaAlias;
    }

    public void setMangaAlias(String mangaAlias) {
        this.mangaAlias = mangaAlias;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(String lastChapter) {
        this.lastChapter = lastChapter;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    @Override
    public int compareTo(Manga another) {
        return name.trim().compareTo(another.getName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static class MangaBuilder {
        private Manga manga;

        public MangaBuilder() {
            manga = new Manga();
        }

        public static MangaBuilder createManga() {
            return new MangaBuilder();
        }

        public MangaBuilder withName(String name) {
            manga.setName(name);
            return this;
        }

        public MangaBuilder withMangaAlias(String mangaAlias) {
            manga.setMangaAlias(mangaAlias);
            return this;
        }

        public MangaBuilder withProvider(String provider) {
            manga.setProvider(provider);
            return this;
        }

        public MangaBuilder withLastChapter(String lastChapter) {
            manga.setLastChapter(lastChapter);
            return this;
        }

        public MangaBuilder withId(long id) {
            manga.setId(id);
            return this;
        }

        public Manga build() {
            return manga;
        }
    }
}
