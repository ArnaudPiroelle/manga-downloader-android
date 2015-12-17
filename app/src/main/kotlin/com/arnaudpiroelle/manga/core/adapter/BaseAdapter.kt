package com.arnaudpiroelle.manga.core.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arnaudpiroelle.manga.core.ui.presenter.view.BaseItemView
import java.util.*

open class BaseAdapter<T, R> @JvmOverloads constructor(context: Context, private val viewResId: Int, data: List<T> = ArrayList<T>()) : android.widget.BaseAdapter() where R : View, R : BaseItemView<T> {
    private val data = ArrayList<T>()
    private val layoutInflater: LayoutInflater

    init {
        this.data.addAll(data)

        layoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return getData().size
    }

    override fun getItemId(position: Int): Long {
        if (position >= 0 && position < getData().size) {
            return getItem(position)!!.hashCode().toLong()
        }
        return INVALID_POSITION.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            view = newView(parent)
        } else {
            view = convertView
        }
        bindView(getItem(position), view as R)
        return view
    }

    override fun getItem(position: Int): T {
        return getData()[position]
    }

    private fun newView(parent: ViewGroup): View {
        return layoutInflater.inflate(viewResId, parent, false)
    }

    private fun bindView(item: T, view: R) {
        view.bindView(item)
    }

    open public fun setData(data: List<T>): Unit {
        this.data.clear()
        this.data.addAll(data)

        notifyDataSetChanged()
    }

    fun remove(o: T) {
        data.remove(o)
        notifyDataSetChanged()
    }

    fun add(o: T) {
        data.add(o)
        notifyDataSetChanged()
    }

    open fun getData(): List<T> {
        return data
    }

    companion object {
        val INVALID_POSITION = -1
    }
}

