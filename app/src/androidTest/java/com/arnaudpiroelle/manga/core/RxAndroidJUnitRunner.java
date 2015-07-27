package com.arnaudpiroelle.manga.core;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnitRunner;

import rx.plugins.RxJavaPlugins;

public class RxAndroidJUnitRunner extends AndroidJUnitRunner {
    @Override
    public void onCreate(Bundle arguments) {
        RxJavaPlugins.getInstance().registerObservableExecutionHook(RxIdlingResource.get());

        super.onCreate(arguments);
    }
}

