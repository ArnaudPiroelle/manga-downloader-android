/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arnaudpiroelle.manga.ui.widgets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.lerp
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.CornerTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment

class TopRoundedBackgroundView : View {
    private val shapeDrawable = MaterialShapeDrawable()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TopRoundedBackgroundView)
        color = a.getColor(R.styleable.TopRoundedBackgroundView_backgroundColor, Color.MAGENTA)
        maxRadius = a.getDimension(R.styleable.TopRoundedBackgroundView_radius, 0f)
        a.recycle()

        background = shapeDrawable
        syncCutSize()
    }

    var color: Int = Color.MAGENTA
        set(value) {
            shapeDrawable.setTint(value)
            field = value
        }

    var maxRadius: Float = 0f
        set(value) {
            field = value
            syncCutSize()
        }

    var cutProgress: Float = 0f
        set(value) {
            field = value
            syncCutSize()
        }

    private fun syncCutSize() {
        val newCutSize = lerp(0f, maxRadius, cutProgress)

        shapeDrawable.shapeAppearanceModel = shapeDrawable.shapeAppearanceModel.toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED, newCutSize)
                .setTopRightCorner(CornerFamily.ROUNDED, newCutSize)
                .build()
    }
}