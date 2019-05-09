package com.arnaudpiroelle.manga.ui.history

import android.text.format.DateUtils.*
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.inflate
import com.arnaudpiroelle.manga.data.model.History
import kotlinx.android.synthetic.main.item_view_history.view.*


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
            itemView.history_sublabel.text = history.sublabel
            itemView.history_date.text = getRelativeTimeDisplayString(history.date.time, System.currentTimeMillis())
        }

        private fun getRelativeTimeDisplayString(referenceTime: Long, now: Long): CharSequence {
            val difference = now - referenceTime

            return if (difference in 0..MINUTE_IN_MILLIS) {
                itemView.context.getString(R.string.just_now)
            } else {
                getRelativeTimeSpanString(
                        referenceTime,
                        now,
                        MINUTE_IN_MILLIS,
                        FORMAT_ABBREV_RELATIVE)
            }
        }

    }

}

object HistoryDiff : DiffUtil.ItemCallback<History>() {
    override fun areItemsTheSame(oldItem: History, newItem: History) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: History, newItem: History) = oldItem == newItem
}