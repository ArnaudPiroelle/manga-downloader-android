package com.arnaudpiroelle.manga.ui.history

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.adapter.BaseAdapter
import com.arnaudpiroelle.manga.model.History
import kotlinx.android.synthetic.main.fragment_listing_history.*

class HistoryFragment : Fragment(), HistoryPresenter.HistoryListingCallback, SwipeRefreshLayout.OnRefreshListener {

    lateinit var presenter: HistoryPresenter
    lateinit var adapter: BaseAdapter<History, HistoryView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GRAPH.inject(this)

        setHasOptionsMenu(true)

        presenter = HistoryPresenter(this)
        adapter = BaseAdapter<History, HistoryView>(activity, R.layout.item_view_history)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_listing_history, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_history.emptyView = history_empty
        list_history.adapter = adapter
        swipe_refresh.setOnRefreshListener(this)
    }


    override fun onResume() {
        super.onResume()

        activity.setTitle(R.string.title_history)

        presenter.list()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clean_history -> {
                cleanHistory()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cleanHistory() {
        presenter.cleanHistory()
    }

    override fun onListingLoading() {
        swipe_refresh.isRefreshing = true
    }

    override fun onListingLoaded(histories: List<History>) {
        swipe_refresh.isRefreshing = false

        adapter.setData(histories)
    }

    override fun onRefresh() {
        presenter.list()
    }
}
