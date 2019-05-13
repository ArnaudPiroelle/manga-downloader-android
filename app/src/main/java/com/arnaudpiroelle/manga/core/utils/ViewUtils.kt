package com.arnaudpiroelle.manga.core.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}