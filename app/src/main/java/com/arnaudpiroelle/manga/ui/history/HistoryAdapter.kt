package com.arnaudpiroelle.manga.ui.history

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.inflate
import com.arnaudpiroelle.manga.data.model.History
import kotlinx.android.synthetic.main.item_view_history.view.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : PagedListAdapter<History, HistoryAdapter.HistoryViewHolder>(HistoryDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(parent.inflate(R.layout.item_view_history, false))
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(history: History?) {
            if (history == null) {
                return
            }

            itemView.history_label.text = history.label
            itemView.history_date.text = simpleDateFormat.format(history.date)
        }

        companion object {
            private val simpleDateFormat = SimpleDateFormat("dd/MM/yy hh:mm", Locale.getDefault())
        }
    }

}

object HistoryDiff : DiffUtil.ItemCallback<History>() {
    override fun areItemsTheSame(oldItem: History, newItem: History) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: History, newItem: History) = oldItem == newItem
}