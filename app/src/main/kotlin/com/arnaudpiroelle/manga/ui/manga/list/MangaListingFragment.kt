package com.arnaudpiroelle.manga.ui.manga.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.data.ChapterRepository
import com.arnaudpiroelle.manga.data.MangaRepository
import com.arnaudpiroelle.manga.data.PageRepository
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.data.model.MangaWithCover
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaActivity
import kotlinx.android.synthetic.main.fragment_listing_manga.*
import javax.inject.Inject


class MangaListingFragment : Fragment(), MangaListingContract.View {

    @Inject
    lateinit var mangaRepository: MangaRepository
    @Inject
    lateinit var chapterRepository: ChapterRepository
    @Inject
    lateinit var pageRepository: PageRepository

    private val userActionsListener: MangaListingContract.UserActionsListener  by lazy { MangaListingPresenter(this, mangaRepository, chapterRepository, pageRepository) }
    private val adapter by lazy { MangaListingAdapter(activity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inject()

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_listing_manga, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_manga.layoutManager = GridLayoutManager(activity, 3)
        list_manga.adapter = adapter

        toolbar.setTitle(R.string.title_mymangas)
        toolbar.inflateMenu(R.menu.menu_manga_listing)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_download -> {
                    //updateScheduling(requireContext(), preferencesHelper)
                    true
                }
                else -> false
            }
        }

        action_add_manga.setOnClickListener { userActionsListener.addManga() }

    }

    override fun onResume() {
        super.onResume()

        userActionsListener.register()
    }

    override fun onPause() {
        userActionsListener.unregister()

        super.onPause()
    }

    override fun displayMangas(mangas: List<MangaWithCover>) {
        adapter.update(mangas)
    }

    override fun openNewMangaWizard() {
        startActivity(Intent(activity, AddMangaActivity::class.java))
    }

    override fun displayModificationDialog(manga: Manga) {
        /*val modifyMangaDialogFragment = ModifyMangaDialogFragment.newInstance(manga.provider, manga.name, manga.mangaAlias, manga.lastChapter)
        modifyMangaDialogFragment.show(childFragmentManager, null)*/
    }

    override fun displayRemoveConfirmation(manga: Manga) {
        AlertDialog.Builder(requireContext())
                .setTitle("Remove ${manga.name} ?")
                .setMessage("Do you really want to remove this manga?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    userActionsListener.remove(manga)
                }
                .setNegativeButton(android.R.string.no, null)
                .show()


    }
}
