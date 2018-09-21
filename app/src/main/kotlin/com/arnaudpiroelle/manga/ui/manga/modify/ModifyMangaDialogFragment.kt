package com.arnaudpiroelle.manga.ui.manga.modify

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import androidx.fragment.app.DialogFragment
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.Chapter
import com.arnaudpiroelle.manga.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.core.db.dao.TaskDao
import com.arnaudpiroelle.manga.core.inject.inject
import javax.inject.Inject

class ModifyMangaDialogFragment : DialogFragment(), ModifyMangaContract.View {

    @Inject
    lateinit var mangaDao: MangaDao

    @Inject
    lateinit var taskDao: TaskDao

    private val userActionsListener: ModifyMangaContract.UserActionsListener by lazy { ModifyMangaPresenter(this, ProviderRegistry, mangaDao, taskDao) }
    private val adapter = ChaptersAdapter()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        inject()

        val dialogBuilder = AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_select_chapter)
                .setSingleChoiceItems(adapter, 0) { _, _ -> }

        val provider: String? = arguments?.getString(EXTRA_MANGA_PROVIDER)
        val name: String? = arguments?.getString(EXTRA_MANGA_NAME)
        val mangaAlias: String? = arguments?.getString(EXTRA_MANGA_ALIAS)
        val mangaLastChapter: String? = arguments?.getString(EXTRA_MANGA_LAST_CHAPTER)

        if (provider == null || name == null || mangaAlias == null || mangaLastChapter == null) {
            dismiss()
        } else {
            dialogBuilder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                val checkedItemPosition = (dialog as AlertDialog).listView.checkedItemPosition
                userActionsListener.selectChapter(provider, name, mangaAlias, adapter.getItem(checkedItemPosition).chapterNumber)
            }
            userActionsListener.findChapters(provider, mangaAlias, mangaLastChapter)
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
        const val EXTRA_MANGA_PROVIDER = "EXTRA_MANGA_PROVIDER"
        const val EXTRA_MANGA_NAME = "EXTRA_MANGA_NAME"
        const val EXTRA_MANGA_ALIAS = "EXTRA_MANGA_ALIAS"
        const val EXTRA_MANGA_LAST_CHAPTER = "EXTRA_MANGA_LAST_CHAPTER"
        const val EXTRA_FINISH_AFTER = "EXTRA_FINISH_AFTER"

        fun newInstance(provider: String, mangaName: String, mangaAlias: String, lastChapter: String, finishAfter: Boolean = false): ModifyMangaDialogFragment {
            val modifyMangaDialogFragment = ModifyMangaDialogFragment()

            val bundle = Bundle()
            bundle.putString(EXTRA_MANGA_PROVIDER, provider)
            bundle.putString(EXTRA_MANGA_NAME, mangaName)
            bundle.putString(EXTRA_MANGA_ALIAS, mangaAlias)
            bundle.putString(EXTRA_MANGA_LAST_CHAPTER, lastChapter)
            bundle.putBoolean(EXTRA_FINISH_AFTER, finishAfter)
            modifyMangaDialogFragment.arguments = bundle

            return modifyMangaDialogFragment
        }
    }
}
