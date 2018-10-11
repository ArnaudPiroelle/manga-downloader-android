package com.arnaudpiroelle.manga.ui.manga.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.calculateDiff
import com.arnaudpiroelle.manga.data.model.MangaWithCover
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_view_manga.view.*

class MangaListingAdapter(context: Context?, val userActionsListener: MangaListingContract.UserActionsListener) : RecyclerView.Adapter<MangaViewHolder>() {

    private val datas = mutableListOf<MangaWithCover>()
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        return MangaViewHolder(userActionsListener, layoutInflater.inflate(R.layout.item_view_manga, parent, false))
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    fun update(mangas: List<MangaWithCover>) {
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

}

class MangaViewHolder(val userActionsListener: MangaListingContract.UserActionsListener, view: View) : RecyclerView.ViewHolder(view) {
    fun bind(manga: MangaWithCover) {
        itemView.manga_name.text = manga.name

        Glide.with(itemView)
                .load(manga.image)
                .into(itemView.manga_thumbnail)
        //itemView.title.text = manga.name
        //itemView.chapter.text = manga.lastChapter

        itemView.setOnClickListener {
            userActionsListener.openMangaDetails(manga)
        }
    }
}