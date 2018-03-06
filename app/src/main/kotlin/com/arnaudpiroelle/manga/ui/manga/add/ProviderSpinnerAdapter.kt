package com.arnaudpiroelle.manga.ui.manga.add

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.provider.MangaProvider


class ProviderSpinnerAdapter(val context: Context) : BaseAdapter() {

    private var mItems: List<Provider> = arrayListOf()

    override fun getCount(): Int {
        return mItems.size
    }

    override fun getItem(position: Int): MangaProvider {
        return mItems[position].mangaProvider
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View = if (convertView == null || convertView.tag.toString() != "DROPDOWN") {
            val viewItem = LayoutInflater.from(context).inflate(R.layout.toolbar_spinner_item_dropdown, parent, false)
            viewItem.tag = "DROPDOWN"
            viewItem
        } else {
            convertView
        }


        val textView: TextView = view.findViewById(android.R.id.text1) as TextView
        textView.text = getTitle(position)

        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = if (convertView == null || convertView.tag.toString() != "DROPDOWN") {
            val viewItem = LayoutInflater.from(context).inflate(R.layout.toolbar_spinner_item_actionbar, parent, false)
            viewItem.tag = "NON_DROPDOWN"
            viewItem
        } else {
            convertView
        }

        val textView = view.findViewById(android.R.id.text1) as TextView
        textView.text = getTitle(position)
        return view
    }

    private fun getTitle(position: Int): String {
        return if (position >= 0 && position < mItems.size) mItems[position].name else ""
    }

    fun update(datas: Map<String, MangaProvider>) {
        mItems = datas.map { Provider(it.key, it.value) }
        notifyDataSetChanged()
    }

    data class Provider(val name: String, val mangaProvider: MangaProvider)

}