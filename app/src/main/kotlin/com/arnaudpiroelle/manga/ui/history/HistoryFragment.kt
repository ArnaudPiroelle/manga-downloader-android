package com.arnaudpiroelle.manga.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.data.core.db.dao.HistoryDao
import com.arnaudpiroelle.manga.data.model.History
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

        inject()

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_listing_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_history.layoutManager = LinearLayoutManager(activity)
        list_history.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.HORIZONTAL))
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
