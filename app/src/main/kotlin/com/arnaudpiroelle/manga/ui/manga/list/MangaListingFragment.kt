package com.arnaudpiroelle.manga.ui.manga.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.data.model.Manga
import com.arnaudpiroelle.manga.ui.home.setActionBar
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingFragmentDirections.actionShowDetails
import kotlinx.android.synthetic.main.fragment_listing_manga.*
import kotlinx.android.synthetic.main.include_bottombar.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MangaListingFragment : Fragment(), MangaListingAdapter.Callback {
    private val navController by lazy { findNavController() }
    private val viewModel: MangaListingViewModel by viewModel()

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

        val adapter = MangaListingAdapter(this)
        list_manga.layoutManager = GridLayoutManager(activity, 3)
        list_manga.adapter = adapter

        add_manga.setOnClickListener {
            navController.navigate(R.id.action_add_manga)
        }

        viewModel.mangas.observe(this, Observer { mangas ->
            adapter.submitList(mangas)
        })

    }

    override fun onResume() {
        super.onResume()

        add_manga.show()
    }

    override fun onPause() {
        add_manga.hide()

        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_manga_listing, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_download -> {
                true
            }
            else -> false
        }
    }

    override fun onMangaSelected(manga: Manga) {
        findNavController().navigate(actionShowDetails(manga.id))
    }

}
