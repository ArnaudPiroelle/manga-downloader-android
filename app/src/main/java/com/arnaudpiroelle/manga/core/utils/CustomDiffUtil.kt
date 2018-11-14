package com.arnaudpiroelle.manga.core.utils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder


fun <VH : ViewHolder, T> Adapter<VH>.calculateDiff(oldList: List<T>, newList: List<T>, predicate: (old: T, new: T) -> Boolean): DiffUtil.DiffResult {
    return DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                predicate(oldList[oldItemPosition], newList[newItemPosition])

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition] == newList[newItemPosition]

    })
}