package com.arnaudpiroelle.ui

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions.open
import android.support.test.espresso.contrib.DrawerMatchers.isClosed
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.LargeTest
import android.view.Gravity
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingActivity
import com.arnaudpiroelle.ui.NavigationViewActions.navigateTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    @Rule @JvmField public var mActivityTestRule = ActivityTestRule(MangaListingActivity::class.java)

    @Test
    fun shouldNavigateInAppByDrawer() {

        onView(withText(R.string.message_empty_manga))
                .check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        onView(withId(R.id.nav_view))
                .perform(navigateTo(R.id.nav_history));

        onView(withText(R.string.message_empty_history))
                .check(matches(isDisplayed()));
    }
}
