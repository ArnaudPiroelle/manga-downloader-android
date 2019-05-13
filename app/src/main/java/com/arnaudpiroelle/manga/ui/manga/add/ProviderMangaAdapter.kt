package com.arnaudpiroelle.manga.ui.manga.add

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.core.utils.inflate
import kotlinx.android.synthetic.main.item_view_provider_manga.view.*

class ProviderMangaAdapter(private val callback: Callback) : ListAdapter<Manga, NewMangaViewHolder>(MangaDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMangaViewHolder {
        return NewMangaViewHolder(parent.inflate(R.layout.item_view_provider_manga, false), callback)
    }

    override fun onBindViewHolder(holder: NewMangaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItemBy(position: Int): Manga {
        return super.getItem(position)
    }

    interface Callback {
        fun onMangaSelected(manga: Manga)
    }
}

class NewMangaViewHolder(view: View, val callback: ProviderMangaAdapter.Callback) : RecyclerView.ViewHolder(view) {
    fun bind(manga: Manga) {
        itemView.title.text = manga.name
        itemView.setOnClickListener {
            callback.onMangaSelected(manga)
        }
    }
}

object MangaDiffUtil : DiffUtil.ItemCallback<Manga>() {
    override fun areItemsTheSame(oldItem: Manga, newItem: Manga) = oldItem.alias == newItem.alias
    override fun areContentsTheSame(oldItem: Manga, newItem: Manga) = oldItem == newItem
}
