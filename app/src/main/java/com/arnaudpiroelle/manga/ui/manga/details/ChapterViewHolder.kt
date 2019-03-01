package com.arnaudpiroelle.manga.ui.manga.details

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.data.model.Chapter
import kotlinx.android.synthetic.main.item_view_chapter.view.*

class ChapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Chapter?) {
        if (item == null) {

        } else {
            itemView.chapter_name.text = item.name
            itemView.chapter_status.text = item.status.name
        }
    }

}