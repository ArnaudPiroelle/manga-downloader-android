package com.arnaudpiroelle.manga.ui.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.bind
import com.arnaudpiroelle.manga.core.utils.distinctUntilChanged
import com.arnaudpiroelle.manga.core.utils.map
import com.arnaudpiroelle.manga.core.utils.setActionBar
import com.arnaudpiroelle.manga.ui.history.HistoryContext.Action.CleanHistoryAction
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_listing_history.*
import kotlinx.android.synthetic.main.include_bottombar.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment() {

    private val viewModel: HistoriesViewModel by viewModel()
    private val adapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_listing_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.setText(R.string.title_history)
        setActionBar(bar)

        val linearLayoutManager = LinearLayoutManager(activity)
        list_history.layoutManager = linearLayoutManager
        list_history.adapter = adapter

        viewModel.state.map { it.visualNotification }.distinctUntilChanged().bind(this, this::onVisualNotificationChanged)

        viewModel.histories.observe(this, Observer {
            adapter.submitList(it)
        })

    }

    override fun onDestroyView() {
        list_history.adapter = null

        super.onDestroyView()
    }

    private fun checkScroll() {
        val adapter = list_history.adapter
        val layoutManager = list_history.layoutManager as LinearLayoutManager
        val totalItems = adapter?.itemCount ?: 0
        val findLastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

        list_history.isNestedScrollingEnabled = findLastCompletelyVisibleItemPosition != totalItems - 1
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clean_history -> {
                viewModel.handle(CleanHistoryAction)
                true
            }
            else -> false
        }
    }

    private fun onVisualNotificationChanged(visualNotification: HistoryContext.VisualNotification?) {
        if (visualNotification != null) {
            Snackbar.make(bar, visualNotification.resId, Snackbar.LENGTH_SHORT).show()
        }
    }

}
