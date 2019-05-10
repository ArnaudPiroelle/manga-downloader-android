package com.arnaudpiroelle.manga.core.utils

fun lerp(startValue: Float, endValue: Float, fraction: Float) = startValue + fraction * (endValue - startValue)