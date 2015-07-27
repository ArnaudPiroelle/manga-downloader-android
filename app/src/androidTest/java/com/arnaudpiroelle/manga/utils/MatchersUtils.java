package com.arnaudpiroelle.manga.utils;

import android.support.test.espresso.matcher.BoundedMatcher;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class MatchersUtils {
    public static Matcher<Object> withMangaProvider(final Matcher<String> stringMatcher) {
        return new BoundedMatcher<Object, MangaProvider>(MangaProvider.class) {
            @Override
            protected boolean matchesSafely(MangaProvider provider) {
                return stringMatcher.matches(provider.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("for provider name ").appendDescriptionOf(stringMatcher);
            }
        };
    }

    public static Matcher<Object> withMangaName(final Matcher<String> stringMatcher) {
        return new BoundedMatcher<Object, Manga>(Manga.class) {
            @Override
            protected boolean matchesSafely(Manga manga) {
                return stringMatcher.matches(manga.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("for manga name ").appendDescriptionOf(stringMatcher);
            }
        };
    }

    public static Matcher<Object> withMangaLastChapter(final Matcher<String> stringMatcher) {
        return new BoundedMatcher<Object, Manga>(Manga.class) {
            @Override
            protected boolean matchesSafely(Manga manga) {
                return stringMatcher.matches(manga.getLastChapter());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("for manga last chapter ").appendDescriptionOf(stringMatcher);
            }
        };
    }

    public static Matcher<Object> withChapterNumber(final Matcher<String> stringMatcher) {
        return new BoundedMatcher<Object, Chapter>(Chapter.class) {
            @Override
            protected boolean matchesSafely(Chapter chapter) {
                return stringMatcher.matches(chapter.getChapterNumber());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("for chapter name ").appendDescriptionOf(stringMatcher);
            }
        };
    }
}
