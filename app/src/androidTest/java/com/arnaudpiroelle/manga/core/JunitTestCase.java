package com.arnaudpiroelle.manga.core;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitor;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;

import com.squareup.spoon.Spoon;

import org.junit.Rule;

public class JunitTestCase<T extends Activity> {
    @Rule
    public IntentsTestRule<T> mActivityRule;


    public JunitTestCase(Class<T> mainActivity) {
        this.mActivityRule = new IntentsTestRule<>(mainActivity, true);
    }

    protected void takeScreenshot(String tag) {
        Spoon.screenshot(getCurrentActivity(), tag);
    }

    protected Activity getCurrentActivity() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        instrumentation.waitForIdleSync();
        final Activity[] activity = new Activity[1];
        instrumentation.runOnMainSync(() -> {
            ActivityLifecycleMonitor activityLifecycleMonitor = ActivityLifecycleMonitorRegistry.getInstance();
            java.util.Collection<Activity> resumedActivities = activityLifecycleMonitor.getActivitiesInStage(Stage.RESUMED);
            activity[0] = Iterables.getOnlyElement(resumedActivities);
        });
        return activity[0];
    }
}
