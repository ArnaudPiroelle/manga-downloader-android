package com.arnaudpiroelle.manga.ui.manga.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.calculateDiff
import com.arnaudpiroelle.manga.data.model.Manga
import kotlinx.android.synthetic.main.item_view_manga.view.*

class MangaListingAdapter(context: Context?) : RecyclerView.Adapter<MangaViewHolder>() {

    private val datas = mutableListOf<Manga>()
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        return MangaViewHolder(layoutInflater.inflate(R.layout.item_view_manga, parent, false))
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    fun update(mangas: List<Manga>) {
        val calculateDiff = calculateDiff(datas, mangas) { old, new ->
            old.id == new.id
        }
        datas.clear()
        datas.addAll(mangas)
        calculateDiff.dispatchUpdatesTo(this)
    }

    override fun getItemId(position: Int): Long {
        return datas[position].id
    }

    fun getItem(position: Int): Manga {
        return datas[position]
    }
}

class MangaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(manga: Manga) {
        itemView.manga_name.text = manga.name
        //itemView.title.text = manga.name
        //itemView.chapter.text = manga.lastChapter
    }
}