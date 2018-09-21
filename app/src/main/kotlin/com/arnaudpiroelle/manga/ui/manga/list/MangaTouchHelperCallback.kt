package com.arnaudpiroelle.manga.ui.manga.list

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_view_manga.view.*


class MangaTouchHelperCallback(private val adapter: MangaListingAdapter, private val userActionsListener: MangaListingContract.UserActionsListener)
    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val manga = adapter.getItem(position)
        when (direction) {
            ItemTouchHelper.LEFT -> userActionsListener.askForRemove(manga)
            ItemTouchHelper.RIGHT -> userActionsListener.askForModify(manga)
        }

        adapter.notifyItemChanged(position)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        val mangaViewHolder = viewHolder as? MangaViewHolder
        mangaViewHolder?.apply {
            itemView.content.apply { translationX = 0f }
            itemView.edit_button.apply { visibility = View.GONE }
            itemView.remove_button.apply { visibility = View.GONE }
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        val mangaViewHolder = viewHolder as? MangaViewHolder
        mangaViewHolder?.apply {
            itemView.content.apply { translationX = 0f }
            itemView.edit_button.apply { visibility = View.VISIBLE }
            itemView.remove_button.apply { visibility = View.VISIBLE }
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val mangaViewHolder = viewHolder as? MangaViewHolder

        if (dX > 0) {
            val translationX = Math.min(dX, (viewHolder.itemView.width / 4).toFloat())

            mangaViewHolder?.let {
                it.itemView.content.translationX = translationX

                val alpha = (.2 + .8 * Math.abs(dX) / viewHolder.itemView.edit_button.width).toFloat()
                viewHolder.itemView.edit_button.alpha = alpha
            }
        } else {
            val translationX = Math.max(dX, -(viewHolder.itemView.width / 4).toFloat())

            mangaViewHolder?.let {
                it.itemView.content.translationX = translationX

                val alpha = (.2 + .8 * Math.abs(dX) / viewHolder.itemView.remove_button.width).toFloat()
                viewHolder.itemView.remove_button.alpha = alpha
            }
        }
    }
}

