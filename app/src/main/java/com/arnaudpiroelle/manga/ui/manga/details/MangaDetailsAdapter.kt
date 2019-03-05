package com.arnaudpiroelle.manga.ui.manga.details

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.inflate
import com.arnaudpiroelle.manga.data.model.Chapter

class MangaDetailsAdapter(private val callback: ChapterViewHolder.Callback) : PagedListAdapter<Chapter, ChapterViewHolder>(ChapterDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder(parent.inflate(R.layout.item_view_chapter, false), callback)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ChapterDiff : DiffUtil.ItemCallback<Chapter>() {
    override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem == newItem
    }
}