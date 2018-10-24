package com.arnaudpiroelle.manga.ui.manga.list

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.inflate
import com.arnaudpiroelle.manga.data.model.Manga

class MangaListingAdapter(val callback: Callback) : PagedListAdapter<Manga, MangaViewHolder>(MangaDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        return MangaViewHolder(parent.inflate(R.layout.item_view_manga, false))
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        holder.bind(getItem(position), callback)
    }

    interface Callback {
        fun onMangaSelected(manga: Manga)
    }
}

object MangaDiff : DiffUtil.ItemCallback<Manga>() {
    override fun areItemsTheSame(oldItem: Manga, newItem: Manga): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Manga, newItem: Manga): Boolean {
        return oldItem == newItem
    }
}