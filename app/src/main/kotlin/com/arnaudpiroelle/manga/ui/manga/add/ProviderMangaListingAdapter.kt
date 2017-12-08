package com.arnaudpiroelle.manga.ui.manga.add

import android.content.Context
import android.widget.SectionIndexer
import com.arnaudpiroelle.manga.core.adapter.BaseAdapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.ui.manga.list.view.MangaView
import java.util.*


class ProviderMangaListingAdapter(val context: Context, val layoutId: Int) :
        BaseAdapter<Manga, MangaView>(context, layoutId), SectionIndexer {

    companion object {
        private val OTHER_SECTION = '#'
    }

    internal var mSectionIndices: IntArray? = null
    private var mSectionLetters: Array<Char?>? = null
    private var mFilterQuery: String? = null

    override fun getData(): List<Manga> {
        return super.getData()
                .filter { mFilterQuery == null || it.name!!.toLowerCase().contains(mFilterQuery.toString().toLowerCase()) }
                .sortedBy { it.name }
    }

    override fun setData(data: List<Manga>) {
        mSectionIndices = getSectionIndices(data)
        mSectionLetters = getSectionLetters(data, mSectionIndices!!)

        super.setData(data)
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        var index = sectionIndex

        if (index >= mSectionIndices!!.size) {
            index = mSectionIndices!!.size - 1
        }
        if (index < 0) {
            index = 0
        }
        if (mSectionIndices!!.size == 0) {
            return 0
        }
        return mSectionIndices!![index]
    }

    override fun getSectionForPosition(position: Int): Int {
        for (i in mSectionIndices!!.indices) {
            if (position < mSectionIndices!![i]) {
                return i - 1
            }
        }
        return mSectionIndices!!.size - 1
    }

    override fun getSections(): Array<Char?>? {
        return mSectionLetters
    }

    internal fun getSectionIndices(items: List<Manga>): IntArray {
        var lastFirstChar = ' '
        val sectionIndices = ArrayList<Int>()

        for (i in items.indices) {
            var curChar = items[i].name!![0]
            if (!Character.isLetter(curChar)) {
                curChar = OTHER_SECTION
            }

            if (curChar != lastFirstChar) {
                lastFirstChar = curChar.toUpperCase()
                sectionIndices.add(i)
            }
        }

        if (sectionIndices.size == 1) {
            sectionIndices.clear()
        }

        val sections = IntArray(sectionIndices.size)
        for (i in sectionIndices.indices) {
            sections[i] = sectionIndices[i]
        }
        return sections
    }

    internal fun getSectionLetters(items: List<Manga>, sectionIndices: IntArray): Array<Char?> {
        val length = sectionIndices.size
        val letters = arrayOfNulls<Char>(length)
        for (i in 0..length - 1) {
            var curChar = items[sectionIndices[i]].name!![0]
            if (!Character.isLetter(curChar)) {
                curChar = OTHER_SECTION
            }
            letters[i] = curChar.toUpperCase()
        }
        return letters
    }

    fun filter(query: String) {
        mFilterQuery = query
        notifyDataSetChanged()
    }
}