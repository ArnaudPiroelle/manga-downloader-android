package com.arnaudpiroelle.manga.ui.manga.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.data.model.Manga
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_view_manga.view.*

class MangaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(manga: Manga?, callback: MangaListingAdapter.Callback) {

        if (manga == null) {
            itemView.manga_name.text = "Loading"
        } else {
            itemView.manga_name.text = manga.name

            Glide.with(itemView)
                    .load(manga.thumbnail)
                    .into(itemView.manga_thumbnail)

            itemView.setOnClickListener {
                callback.onMangaClick(manga)
            }
        }


    }
}