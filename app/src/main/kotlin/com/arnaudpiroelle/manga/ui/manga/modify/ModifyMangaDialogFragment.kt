package com.arnaudpiroelle.manga.ui.manga.modify

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class ModifyMangaDialogFragment : DialogFragment() {

    @Inject lateinit var providerRegistry: ProviderRegistry;

    private val mAdapter = ChaptersAdapter()

    public var manga: Manga? = null
    public var onChapterChoosenListener: ((Chapter) -> Unit)? = { }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        GRAPH.inject(this)

        val dialogBuilder = AlertDialog.Builder(activity)

        dialogBuilder
                .setTitle(R.string.dialog_select_chapter)
                .setPositiveButton(android.R.string.ok, { dialog, which ->
                    val checkedItemPosition = (dialog as AlertDialog).listView.checkedItemPosition

                    onChapterChoosenListener?.invoke(mAdapter.getItem(checkedItemPosition))
                })

        dialogBuilder.setSingleChoiceItems(mAdapter, 0, { dialog, which -> })
        val dialog = dialogBuilder.create()

        Observable.create<List<Chapter>> { subscriber ->
            subscriber.onNext(providerRegistry.find(manga!!.provider!!)?.findChapters(manga!!))
            subscriber.onCompleted()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { chapters ->

                    val allChapter = Chapter()
                    allChapter.chapterNumber = "all"

                    var allChapters: List<Chapter> = ArrayList<Chapter>().apply {
                        add(allChapter)
                        addAll(chapters)
                    }

                    mAdapter.datas = allChapters
                    mAdapter.notifyDataSetChanged()

                    val indexOfRaw = allChapters.indexOfRaw(allChapters.find { it.chapterNumber.equals(manga!!.lastChapter) })
                    dialog.listView.setSelection(indexOfRaw)
                    dialog.listView.setItemChecked(indexOfRaw, true)
                }


        return dialog
    }

    private inner class ChaptersAdapter() : BaseAdapter() {

        var datas: List<Chapter> = arrayListOf()

        override fun getCount(): Int {
            return datas.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view: CheckedTextView
            if (convertView == null) {
                view = LayoutInflater.from(activity).inflate(R.layout.select_dialog_singlechoice_material, parent, false) as CheckedTextView;
            } else {
                view = convertView as CheckedTextView
            }

            view.text = getItem(position).chapterNumber

            return view
        }

        override fun getItem(position: Int): Chapter {
            return datas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

    }
}
