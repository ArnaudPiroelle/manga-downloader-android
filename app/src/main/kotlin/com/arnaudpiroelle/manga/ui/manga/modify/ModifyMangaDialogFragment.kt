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
import com.arnaudpiroelle.manga.core.db.MangaDao
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.model.network.Chapter
import javax.inject.Inject

class ModifyMangaDialogFragment : DialogFragment(), ModifyMangaContract.View {

    @Inject
    lateinit var providerRegistry: ProviderRegistry
    @Inject
    lateinit var mangaDao: MangaDao

    private val userActionsListener: ModifyMangaContract.UserActionsListener by lazy { ModifyMangaPresenter(this, providerRegistry, mangaDao) }
    private val adapter = ChaptersAdapter()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        GRAPH.inject(this)

        val dialogBuilder = AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_select_chapter)
                .setSingleChoiceItems(adapter, 0) { _, _ -> }

        val manga: Manga? = arguments?.getParcelable(EXTRA_MANGA) as Manga
        if (manga == null) {
            dismiss()
        } else {
            dialogBuilder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                val checkedItemPosition = (dialog as AlertDialog).listView.checkedItemPosition
                userActionsListener.selectChapter(manga, adapter.getItem(checkedItemPosition))
            }
            userActionsListener.findChapters(manga)
        }

        return dialogBuilder.create()
    }

    override fun displayChapters(chapters: List<Chapter>, selection: Int) {
        adapter.update(chapters)

        val listView = (dialog as AlertDialog).listView
        listView.setSelection(selection)
        listView.setItemChecked(selection, true)
    }

    override fun closeWizard() {
        val finishAfter = arguments?.getBoolean(EXTRA_FINISH_AFTER) ?: false
        if (finishAfter) {
            activity?.finish()
        }
    }

    private inner class ChaptersAdapter : BaseAdapter() {

        private var datas = mutableListOf<Chapter>()

        override fun getCount(): Int {
            return datas.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val view = if (convertView == null) {
                LayoutInflater.from(activity).inflate(android.R.layout.select_dialog_singlechoice, parent, false) as CheckedTextView
            } else {
                convertView as CheckedTextView
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

        fun update(chapters: List<Chapter>) {
            datas.clear()
            datas.addAll(chapters)
            notifyDataSetChanged()
        }

    }

    companion object {
        const val EXTRA_MANGA = "EXTRA_MANGA"
        const val EXTRA_FINISH_AFTER = "EXTRA_FINISH_AFTER"

        fun newInstance(manga: Manga, finishAfter: Boolean = false): ModifyMangaDialogFragment {
            val modifyMangaDialogFragment = ModifyMangaDialogFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_MANGA, manga)
            bundle.putBoolean(EXTRA_FINISH_AFTER, finishAfter)
            modifyMangaDialogFragment.arguments = bundle

            return modifyMangaDialogFragment
        }
    }
}
