package com.arnaudpiroelle.manga.ui.manga.list.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.arnaudpiroelle.manga.core.ui.presenter.view.BaseItemView
import com.arnaudpiroelle.manga.model.Manga
import kotlinx.android.synthetic.item_view_manga.view.*

class MangaView : FrameLayout, BaseItemView<Manga> {

    private lateinit var manga: Manga

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun bindView(manga: Manga) {
        this.manga = manga

        title.text = manga.name

        val lastChapter = manga.lastChapter
        if (lastChapter != null && !lastChapter.isEmpty()) {
            if (chapter != null) {
                chapter!!.text = lastChapter
                chapter!!.visibility = View.VISIBLE
            }
        } else {
            if (chapter != null) {
                chapter!!.text = ""
            }
        }

    }

}
