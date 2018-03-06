package com.arnaudpiroelle.manga.ui.history

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.db.HistoryDao
import com.arnaudpiroelle.manga.model.db.History
import com.arnaudpiroelle.manga.ui.history.HistoryContract.UserActionsListener
import kotlinx.android.synthetic.main.fragment_listing_history.*
import javax.inject.Inject

class HistoryFragment : Fragment(), HistoryContract.View {

    @Inject
    lateinit var historyDao: HistoryDao

    private val userActionsListener: UserActionsListener by lazy { HistoryPresenter(this, historyDao) }
    private val adapter by lazy { HistoryAdapter(activity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GRAPH.inject(this)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_listing_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_history.layoutManager = LinearLayoutManager(activity)
        list_history.adapter = adapter

        toolbar.setTitle(R.string.title_history)
        toolbar.inflateMenu(R.menu.menu_history)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_clean_history -> {
                    userActionsListener.cleanHistory()
                    true
                }
                else -> false
            }
        }

    }


    override fun onResume() {
        super.onResume()

        userActionsListener.register()
    }

    override fun onPause() {
        userActionsListener.unregister()

        super.onPause()
    }

    override fun displayHistories(histories: List<History>) {
        adapter.update(histories)
    }


}
