package com.arnaudpiroelle.manga.model;

import java.util.List;

public class Chapter {
    private String name;
    private String mangaAlias;
    private String chapterNumber;
    private List<Page> pages;


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
