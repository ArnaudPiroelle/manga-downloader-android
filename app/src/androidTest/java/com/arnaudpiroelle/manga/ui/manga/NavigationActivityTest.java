package com.arnaudpiroelle.manga.ui.manga;

import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.ComponentNameMatchers;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.matcher.ViewMatchers.Visibility;
import android.support.test.runner.AndroidJUnit4;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.core.JunitTestCase;
import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.service.DownloadService;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.GONE;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.arnaudpiroelle.manga.utils.MatchersUtils.withChapterNumber;
import static com.arnaudpiroelle.manga.utils.MatchersUtils.withMangaLastChapter;
import static com.arnaudpiroelle.manga.utils.MatchersUtils.withMangaName;
import static com.arnaudpiroelle.manga.utils.MatchersUtils.withMangaProvider;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasToString;

@RunWith(AndroidJUnit4.class)
public class NavigationActivityTest extends JunitTestCase<NavigationActivity> {

    public NavigationActivityTest() {
        super(NavigationActivity.class);
    }

    @After
    public void tearDown() throws Exception {
        mActivityRule.getActivity().deleteDatabase("sprinkles.db");
    }


    @Test
    public void shouldBeAbleToDisplayNavigationDrawer() throws Exception {
        openNavigationDrawer();
        takeScreenshot("Display_navigation_drawer_menu");
    }

    @Test
    public void shouldBeAbleToAddManga() throws Exception {
        checkEmptyViewIs(R.id.manga_empty, VISIBLE);
        takeScreenshot("Display_empty_list_of_manga");

        clickOnAddManga();
        takeScreenshot("Display_providers_list");

        selectMangaProvider("JapScan");
        takeScreenshot("Display_provider_mangas_list");

        selectManga("Naruto");
        takeScreenshot("Display_manga_chapters");

        selectChapter("710");

        checkEmptyViewIs(R.id.manga_empty, GONE);
        checkMangaIsDisplayed("Naruto", "710");
        takeScreenshot("Display_manga_added");

    }

    private void openNavigationDrawer() {
        onView(withContentDescription("Navigate up"))
                .perform(click());
    }

    private void checkMangaIsDisplayed(String mangaName, String chapterNumber) {
        onData(allOf(is(instanceOf(Manga.class)), withMangaName(hasToString(mangaName)), withMangaLastChapter(hasToString(chapterNumber))))
                .inAdapterView(withId(R.id.list_manga))
                .check(matches(isDisplayed()));
    }

    private void checkEmptyViewIs(int viewId, Visibility visibility) {
        onView(withId(viewId))
                .check(matches(withEffectiveVisibility(visibility)));
    }

    private void selectChapter(String chapterNumber) {
        onData(allOf(is(instanceOf(Chapter.class)), withChapterNumber(hasToString(chapterNumber))))
                .inAdapterView(withId(R.id.list_provider_manga_chapters))
                .perform(click());
    }

    private void selectManga(String mangaName) {
        onData(allOf(is(instanceOf(Manga.class)), withMangaName(hasToString(mangaName))))
                .inAdapterView(withId(R.id.list_provider_mangas))
                .perform(click());

    }

    private void selectMangaProvider(String providerName) {
        onData(allOf(is(instanceOf(MangaProvider.class)), withMangaProvider(hasToString(providerName))))
                .inAdapterView(withId(R.id.list_provider))
                .check(matches(isCompletelyDisplayed()))
                .perform(click());
    }

    private void clickOnAddManga() {
        onView(withId(R.id.action_add_manga))
                .perform(click());
    }


}