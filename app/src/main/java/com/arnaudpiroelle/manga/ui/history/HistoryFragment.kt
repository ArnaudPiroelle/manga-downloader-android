package com.arnaudpiroelle.manga.ui.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.setActionBar
import kotlinx.android.synthetic.main.fragment_listing_history.*
import kotlinx.android.synthetic.main.include_bottombar.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment() {

    private val viewModel: HistoriesViewModel by viewModel()
    private val adapter by lazy { HistoryAdapter() }

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

        list_history.layoutManager = LinearLayoutManager(activity)
        list_history.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.HORIZONTAL))
        list_history.adapter = adapter

        viewModel.histories.observe(this, Observer {
            adapter.submitList(it)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clean_history -> {
                viewModel.cleanHistory()
                true
            }
            else -> false
        }
    }

}
