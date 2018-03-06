package com.arnaudpiroelle.manga.ui.manga.add

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.model.db.Manga
import kotlinx.android.synthetic.main.item_view_provider_manga.view.*

class ProviderMangaAdapter(context: Context, private val userActionsListener: AddMangaContract.UserActionsListener) : RecyclerView.Adapter<NewMangaViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)
    private val datas = mutableListOf<Manga>()
    private val filtererDatas = mutableListOf<Manga>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMangaViewHolder {
        return NewMangaViewHolder(layoutInflater.inflate(R.layout.item_view_provider_manga, parent, false))
    }

    override fun getItemCount(): Int {
        return filtererDatas.size
    }

    override fun onBindViewHolder(holder: NewMangaViewHolder, position: Int) {
        holder.bind(filtererDatas[position], userActionsListener)
    }

    fun update(mangas: List<Manga>) {
        val calculateDiff = DiffUtil.calculateDiff(MangaDiffUtil(datas, mangas))

        datas.clear()
        datas.addAll(mangas)
        filtererDatas.clear()
        filtererDatas.addAll(mangas)

        calculateDiff.dispatchUpdatesTo(this)
    }

    fun filter(name: String?) {
        val filteredList = datas.filter { name == null || (it.name?.contains(name, true) ?: false) }
        val calculateDiff = DiffUtil.calculateDiff(MangaDiffUtil(filtererDatas, filteredList))
        filtererDatas.clear()
        filtererDatas.addAll(filteredList)
        calculateDiff.dispatchUpdatesTo(this)
    }
}

class NewMangaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(manga: Manga, userActionsListener: AddMangaContract.UserActionsListener) {
        itemView.title.text = manga.name
        itemView.setOnClickListener {
            userActionsListener.selectManga(manga)
        }
    }
}

class MangaDiffUtil(private val oldList: List<Manga>, private val newList: List<Manga>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].mangaAlias == newList[newItemPosition].mangaAlias
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}
