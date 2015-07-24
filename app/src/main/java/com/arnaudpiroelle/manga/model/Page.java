package com.arnaudpiroelle.manga.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Page implements Parcelable{
    private String mangaAlias;
    private String chapterNumber;
    private String pageNumber;
    private String pageName;
    private String extension;

    public Page() {

    }

    private Page(Parcel in) {
        mangaAlias = in.readString();
        chapterNumber = in.readString();
        pageNumber = in.readString();
        pageName = in.readString();
        extension = in.readString();
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

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mangaAlias);
        dest.writeString(chapterNumber);
        dest.writeString(pageNumber);
        dest.writeString(pageName);
        dest.writeString(extension);
    }

    public static final Parcelable.Creator<Page> CREATOR = new Parcelable.Creator<Page>() {
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        public Page[] newArray(int size) {
            return new Page[size];
        }
    };

    public static class PageBuilder {
        private Page page;

        public PageBuilder() {
            page = new Page();
        }

        public static PageBuilder createPage() {
            return new PageBuilder();
        }

        public PageBuilder withPageNumber(String pageNumber) {
            page.setPageNumber(pageNumber);
            return this;
        }

        public PageBuilder withMangaAlias(String mangaAlias) {
            page.setMangaAlias(mangaAlias);
            return this;
        }

        public PageBuilder withChapterNumber(String chapterNumber) {
            page.setChapterNumber(chapterNumber);
            return this;
        }

        public PageBuilder withExtenstion(String extension) {
            page.setExtension(extension);
            return this;
        }

        public PageBuilder withName(String name) {
            page.setPageName(name);
            return this;
        }

        public Page build() {
            return page;
        }
    }
}
