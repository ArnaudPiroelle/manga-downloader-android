package com.arnaudpiroelle.manga.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Chapter implements Parcelable {
    private String name;
    private String mangaAlias;
    private String chapterNumber;

    private List<Page> pages;

    public Chapter() {

    }

    private Chapter(Parcel in) {
        name = in.readString();
        mangaAlias = in.readString();
        chapterNumber = in.readString();

        pages = new ArrayList<>();
        in.readTypedList(pages, Page.CREATOR);
    }

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

    public String getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(String chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String computeChapterFolderPath(String baseFolder){
        return baseFolder + "/" + getMangaAlias() + "/" + getChapterNumber();
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mangaAlias);
        dest.writeString(chapterNumber);
        dest.writeTypedList(pages);
    }

    public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>() {
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    public static class ChapterBuilder {
        private Chapter chapter;

        public ChapterBuilder() {
            chapter = new Chapter();
        }

        public static ChapterBuilder createChapter() {
            return new ChapterBuilder();
        }

        public ChapterBuilder withName(String name) {
            chapter.setName(name);
            return this;
        }

        public ChapterBuilder withMangaAlias(String mangaAlias) {
            chapter.setMangaAlias(mangaAlias);
            return this;
        }

        public ChapterBuilder withChapterNumber(String chapterNumber) {
            chapter.setChapterNumber(chapterNumber);
            return this;
        }

        public Chapter build() {
            return chapter;
        }
    }
}
