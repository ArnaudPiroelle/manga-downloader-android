package com.arnaudpiroelle.manga.ui.manga.details

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaudpiroelle.manga.GlideApp
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.bind
import com.arnaudpiroelle.manga.core.utils.distinctUntilChanged
import com.arnaudpiroelle.manga.core.utils.map
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.ui.manga.details.MangaDetailsContext.Action.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_manga_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MangaDetailsFragment : Fragment(), ChapterViewHolder.Callback {
    private val mangaId: Long by lazy { arguments?.getLong("manga_id") ?: -1 }
    private val viewModel: MangaDetailsViewModel by viewModel { parametersOf(mangaId) }
    private val adapter by lazy { MangaDetailsAdapter(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manga_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuInflater = activity?.menuInflater
        if (menuInflater != null) {
            onCreateOptionsMenu(details_toolbar.menu, menuInflater)
            details_toolbar.setOnMenuItemClickListener { item -> onOptionsItemSelected(item) }
        }
        details_toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        container.setOnApplyWindowInsetsListener { _, insets ->
            container.forEachConstraintSet {
                it.constrainHeight(R.id.details_status_bar_anchor, insets.systemWindowInsetTop)
            }
            container.rebuildMotion()
            // Just return insets
            insets
        }

        view.requestApplyInsets()

        container.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE


        chapters_list.layoutManager = LinearLayoutManager(requireContext())
        chapters_list.adapter = adapter

        viewModel.chapters.bind(this, this::onChaptersChanged)
        viewModel.state.map { it.thumbnail }.distinctUntilChanged().bind(this, this::onThumbnailChanged)
        viewModel.state.map { it.title }.distinctUntilChanged().bind(this, this::onTitleChanged)
        viewModel.state.map { "${it.origin} • ${it.year} • ${it.type}\n${it.author}" }.distinctUntilChanged().bind(this, this::onSubTitleChanged)
        viewModel.state.map { it.removed }.distinctUntilChanged().bind(this, this::onRemovedChanged)

        viewModel.handle(LoadMangaInformations)
    }


    override fun onResume() {
        super.onResume()

        container.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout, startId: Int, endId: Int) {
            }

            override fun onTransitionChange(motionLayout: MotionLayout, startId: Int, endId: Int, progress: Float) {
                details_poster_container.visibility = View.VISIBLE
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

            @SuppressLint("RestrictedApi")
            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                when (currentId) {
                    R.id.end -> {
                        details_poster_container.isGone = true
                    }
                    R.id.start -> {
                        details_poster_container.isVisible = true
                    }
                }
            }
        })
    }

    override fun onPause() {
        container.setTransitionListener(null)

        super.onPause()
    }


    private fun onSubTitleChanged(subtitle: String) {
        details_subtitle.text = subtitle
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_manga_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_remove_manga -> viewModel.handle(RemoveMangaAction(mangaId))
            R.id.menu_select_all_chapters -> viewModel.handle(ChangeAllChaptersStatusAction)
        }

        return true
    }

    private fun onRemovedChanged(newRemoved: Boolean) {
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
    }

    private fun onThumbnailChanged(newThumbnail: String) {
        GlideApp.with(this)
                .asBitmap()
                .load(newThumbnail)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (resource != null) {
                            val p = Palette.from(resource).generate()
                            p.vibrantSwatch?.rgb?.let { details_backdrop.setBackgroundColor(it) }
                        }
                        return false
                    }
                })
                .into(details_poster)
    }

    private fun onTitleChanged(newTitle: String) {
        details_title.text = newTitle
    }

    private fun onChaptersChanged(chapters: PagedList<Chapter>) {
        adapter.submitList(chapters)
    }

    override fun onChapterStatusClicked(item: Chapter) {
        viewModel.handle(ChangeChapterStatusAction(item))
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