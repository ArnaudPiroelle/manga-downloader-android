package com.arnaudpiroelle.manga.ui.manga.add

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.utils.inflate


class ProviderSpinnerAdapter : BaseAdapter() {

    private var mItems: List<ProviderRegistry.Provider> = arrayListOf()

    override fun getCount(): Int {
        return mItems.size
    }

    override fun getItem(position: Int): ProviderRegistry.Provider {
        return mItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View = if (convertView == null || convertView.tag.toString() != "DROPDOWN") {
            val viewItem = parent.inflate(R.layout.toolbar_spinner_item_dropdown, false)
            viewItem.tag = "DROPDOWN"
            viewItem
        } else {
            convertView
        }


        val textView: TextView = view.findViewById(android.R.id.text1) as TextView
        textView.text = getTitle(position)

        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = if (convertView == null || convertView.tag.toString() != "DROPDOWN") {
            val viewItem = parent.inflate(R.layout.toolbar_spinner_item_actionbar, false)
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

    fun update(datas: List<ProviderRegistry.Provider>) {
        mItems = datas
        notifyDataSetChanged()
    }
}