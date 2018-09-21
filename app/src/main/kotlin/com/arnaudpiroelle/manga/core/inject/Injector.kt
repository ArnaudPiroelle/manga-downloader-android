package com.arnaudpiroelle.manga.core.inject

import android.app.Activity
import android.app.Application
import android.app.Service
import androidx.fragment.app.Fragment
import com.arnaudpiroelle.manga.core.inject.module.ApplicationModule
import com.arnaudpiroelle.manga.core.inject.module.DatabaseModule
import toothpick.Toothpick

fun Application.inject() {
    val scope = Toothpick.openScope(applicationContext)

    val applicationModule = ApplicationModule(this)
    val databaseModule = DatabaseModule(this)

    scope.installModules(applicationModule, databaseModule)
    Toothpick.inject(this, scope)
}

fun Activity.inject() {
    val scope = Toothpick.openScopes(applicationContext, this)
    Toothpick.inject(this, scope)
}

fun Fragment.inject() {
    val context = activity
    if (context != null) {
        val scope = Toothpick.openScopes(context.applicationContext, activity, this)
        Toothpick.inject(this, scope)
    }
}

fun Service.inject() {
    val scope = Toothpick.openScopes(applicationContext, this)
    Toothpick.inject(this, scope)
}

fun Fragment.uninject() {
    Toothpick.closeScope(this)
}

fun Activity.uninject() {
    Toothpick.closeScope(this)
}