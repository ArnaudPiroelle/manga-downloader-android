package com.arnaudpiroelle.manga.ui.history

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.model.db.History
import kotlinx.android.synthetic.main.item_view_history.view.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(context: Context?) : RecyclerView.Adapter<HistoryViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private val datas = mutableListOf<History>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(layoutInflater.inflate(R.layout.item_view_history, parent, false))
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    fun update(histories: List<History>) {
        val calculateDiff = DiffUtil.calculateDiff(HistoryDiffUtil(datas, histories))
        datas.clear()
        datas.addAll(histories)
        calculateDiff.dispatchUpdatesTo(this)
    }
}

class HistoryDiffUtil(private val oldList: List<History>, private val newList: List<History>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
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

class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(history: History) {
        itemView.history_label.text = history.label
        itemView.history_date.text = simpleDateFormat.format(history.date)
    }

    companion object {
        private val simpleDateFormat = SimpleDateFormat("dd/MM/yy hh:mm", Locale.getDefault())
    }
}
