package com.arnaudpiroelle.manga.ui.manga.list

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.db.MangaDao
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.service.DownloadService.Companion.updateScheduling
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaActivity
import com.arnaudpiroelle.manga.ui.manga.modify.ModifyMangaDialogFragment
import kotlinx.android.synthetic.main.fragment_listing_manga.*
import javax.inject.Inject


class MangaListingFragment : Fragment(), MangaListingContract.View {

    @Inject
    lateinit var mangaDao: MangaDao

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private val userActionsListener: MangaListingContract.UserActionsListener  by lazy { MangaListingPresenter(this, mangaDao) }
    private val adapter by lazy { MangaListingAdapter(activity) }
    private val touchHelper by lazy { ItemTouchHelper(MangaTouchHelperCallback(adapter, userActionsListener)) }

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

        list_manga.layoutManager = LinearLayoutManager(activity)
        list_manga.adapter = adapter

        toolbar.setTitle(R.string.title_mymangas)
        toolbar.inflateMenu(R.menu.menu_manga_listing)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_download -> {
                    updateScheduling(requireContext(), preferencesHelper)
                    true
                }
                else -> false
            }
        }

        touchHelper.attachToRecyclerView(list_manga)

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

    override fun displayMangas(mangas: List<Manga>) {
        adapter.update(mangas)
    }

    override fun openNewMangaWizard() {
        startActivity(Intent(activity, AddMangaActivity::class.java))
    }

    override fun displayModificationDialog(manga: Manga) {
        val modifyMangaDialogFragment = ModifyMangaDialogFragment.newInstance(manga)
        modifyMangaDialogFragment.show(childFragmentManager, null)
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
