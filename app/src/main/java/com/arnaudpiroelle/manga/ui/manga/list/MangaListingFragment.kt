package com.arnaudpiroelle.manga.ui.manga.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.bind
import com.arnaudpiroelle.manga.core.utils.distinctUntilChanged
import com.arnaudpiroelle.manga.core.utils.map
import com.arnaudpiroelle.manga.core.utils.setActionBar
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingContext.Action.DismissNotificationAction
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingContext.Action.StartSyncAction
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingFragmentDirections.actionShowDetails
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_listing_manga.*
import kotlinx.android.synthetic.main.include_bottombar.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MangaListingFragment : Fragment(), MangaListingAdapter.Callback {
    private val navController by lazy { findNavController() }
    private val viewModel: MangaListingViewModel by viewModel()

    private val adapter by lazy { MangaListingAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_listing_manga, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.setText(R.string.title_mymangas)
        setActionBar(bar)

        val gridLayoutManager = GridLayoutManager(activity, 3)
        list_manga.layoutManager = gridLayoutManager
        list_manga.adapter = adapter

        add_manga.setOnClickListener {
            navController.navigate(R.id.action_add_manga)
        }

        viewModel.mangas.bind(this, this::onMangasChanged)
        viewModel.state.map { it.notificationResId }.distinctUntilChanged().bind(this, this::onNotificationChanged)
    }

    private fun checkScroll() {
        val adapter = list_manga.adapter
        val layoutManager = list_manga.layoutManager as GridLayoutManager
        val totalItems = adapter?.itemCount ?: 0
        val findLastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

        list_manga.isNestedScrollingEnabled = findLastCompletelyVisibleItemPosition != totalItems - 1
    }

    override fun onResume() {
        super.onResume()

        add_manga.show()
    }

    override fun onPause() {
        add_manga.hide()

        super.onPause()
    }

    override fun onDestroyView() {
        list_manga.adapter = null

        super.onDestroyView()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_manga_listing, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_download -> {
                viewModel.handle(StartSyncAction)
                true
            }
            else -> false
        }
    }

    override fun onMangaSelected(manga: Manga) {
        findNavController().navigate(actionShowDetails(manga.id))
    }

    private fun onMangasChanged(mangas: PagedList<Manga>) {
        adapter.submitList(mangas)
    }

    private fun onNotificationChanged(notificationResId: Int?) {
        if (notificationResId != null) {
            Snackbar.make(list_manga, notificationResId, Snackbar.LENGTH_SHORT)
                    .show()

            viewModel.handle(DismissNotificationAction)
        }
    }

}
