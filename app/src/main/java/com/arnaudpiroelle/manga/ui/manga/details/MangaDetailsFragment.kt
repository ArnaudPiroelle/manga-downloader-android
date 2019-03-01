package com.arnaudpiroelle.manga.ui.manga.details

import android.os.Bundle
import android.view.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsContext.Action.LoadMangaInformations
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsContext.Action.RemoveMangaAction
import kotlinx.android.synthetic.main.fragment_manga_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MangaDetailsFragment : Fragment() {

    private val mangaId: Long by lazy { arguments?.getLong("manga_id") ?: -1 }
    private val viewModel: MangaDetailsViewModel by viewModel { parametersOf(mangaId) }
    private val adapter by lazy { MangaDetailsAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manga_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuInflater = activity?.menuInflater
        if (menuInflater != null) {
            onCreateOptionsMenu(toolbar.menu, menuInflater)
            toolbar.setOnMenuItemClickListener { item -> onOptionsItemSelected(item) }
        }
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        container.setOnApplyWindowInsetsListener { _, insets ->
            container.forEachConstraintSet {
                it.constrainHeight(R.id.status_bar_anchor, insets.systemWindowInsetTop)
            }
            //    container.rebuildMotion()
            // Just return insets
            insets
        }

        view.requestApplyInsets()

        container.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        plus_summary.setOnClickListener {
            if (summary.maxLines == MAX_LINES_SUMMARY) {
                summary.maxLines = Int.MAX_VALUE
                plus_summary.text = "Moins"
            } else {
                summary.maxLines = MAX_LINES_SUMMARY
                plus_summary.text = "Plus"
            }
        }

        chapters_list.layoutManager = LinearLayoutManager(requireContext())
        chapters_list.adapter = adapter

        /*
        viewModel.chapters.bind(this, this::onChaptersChanged)
        viewModel.state.map { it.title }.distinctUntilChanged().bind(this, this::onTitleChanged)
        viewModel.state.map { it.thumbnail }.distinctUntilChanged().bind(this, this::onThumbnailChanged)
        viewModel.state.map { it.author }.distinctUntilChanged().bind(this, this::onAuthorChanged)
        viewModel.state.map { it.type }.distinctUntilChanged().bind(this, this::onTypeChanged)
        viewModel.state.map { it.origin }.distinctUntilChanged().bind(this, this::onOriginChanged)
        viewModel.state.map { it.year }.distinctUntilChanged().bind(this, this::onYearChanged)
        viewModel.state.map { it.removed }.distinctUntilChanged().bind(this, this::onRemovedChanged)
        viewModel.state.map { it.summary }.distinctUntilChanged().bind(this, this::onSummaryChanged)
        */

        viewModel.handle(LoadMangaInformations)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_manga_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_remove_manga -> viewModel.handle(RemoveMangaAction(mangaId))
        }

        return true
    }

    private fun onSummaryChanged(newSummary: String) {
        //summary.text = newSummary
    }

    private fun onRemovedChanged(newRemoved: Boolean) {
        /*
        if (newRemoved) {
            Snackbar.make(container, "Manga removed", Snackbar.LENGTH_SHORT)
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            transientBottomBar?.removeCallback(this)
                            findNavController().navigateUp()
                        }
                    })
                    .show()
        }
        */
    }

    private fun onYearChanged(newYear: String) {
        //year.text = newYear
    }

    private fun onOriginChanged(newOrigin: String) {
        //origin.text = newOrigin
    }

    private fun onTypeChanged(newType: String) {
        //type.text = newType
    }

    private fun onAuthorChanged(newAuthor: String) {
        //authors.text = newAuthor
    }

    private fun onThumbnailChanged(newThumbnail: String) {
        //Glide.with(this).load(newThumbnail).into(thumbnail)
    }

    private fun onTitleChanged(newTitle: String) {
        //title.text = newTitle
    }

    private fun onChaptersChanged(chapters: PagedList<Chapter>) {
        //adapter.submitList(chapters)
    }

    companion object {
        const val MAX_LINES_SUMMARY = 3
    }

    private inline fun MotionLayout.forEachConstraintSet(f: (ConstraintSet) -> Unit) {
        for (id in constraintSetIds) {
            f(getConstraintSet(id))
        }
    }
}