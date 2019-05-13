package com.arnaudpiroelle.manga.ui.manga.details

import android.content.res.ColorStateList.valueOf
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.data.model.Chapter
import kotlinx.android.synthetic.main.item_view_chapter.view.*

class ChapterViewHolder(view: View, private val callback: Callback?) : RecyclerView.ViewHolder(view) {
    fun bind(item: Chapter?) {
        if (item == null) {

        } else {

            itemView.chapter_name.text = item.name
            itemView.chapter_status.text = item.status.name
            val color = when (item.status) {
                Chapter.Status.WANTED -> R.color.wanted_status
                Chapter.Status.SKIPPED -> R.color.skipped_status
                Chapter.Status.DOWNLOADING -> R.color.downloading_status
                Chapter.Status.DOWNLOADED -> R.color.downloaded_status
                Chapter.Status.ERROR -> R.color.error_status
            }
            itemView.chapter_status.backgroundTintList = valueOf(getColor(itemView.context, color))
            itemView.chapter_status.setOnClickListener {
                callback?.onChapterStatusClicked(item)
            }
        }
    }

    interface Callback {
        fun onChapterStatusClicked(item: Chapter)
    }

}