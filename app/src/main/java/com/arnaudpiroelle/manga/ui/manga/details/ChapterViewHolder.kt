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

            val name = if (volumeRegex.matches(item.number)) {
                computeVolumeName(item)
            } else {
                computeChapterName(item)
            }

            itemView.chapter_name.text = name
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

    private fun computeVolumeName(item: Chapter): String {
        val (number) = volumeRegex.matchEntire(item.number)?.destructured ?: return item.number
        return "Volume $number"
    }

    private fun computeChapterName(item: Chapter): String {
        val values = item.name.split(":")
        return if (values.size > 1) {
            "Chap. ${item.number} : ${values[1]}"
        } else {
            "Chap. ${item.number}"
        }
    }

    interface Callback {
        fun onChapterStatusClicked(item: Chapter)
    }

    companion object {
        val volumeRegex = "volume-([0-9]+)".toRegex()
    }
}