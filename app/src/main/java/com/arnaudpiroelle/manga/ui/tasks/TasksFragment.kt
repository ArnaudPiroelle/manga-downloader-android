package com.arnaudpiroelle.manga.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.setActionBar
import kotlinx.android.synthetic.main.fragment_tasks.*
import kotlinx.android.synthetic.main.include_bottombar.*
import kotlinx.android.synthetic.main.include_title.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TasksFragment : Fragment() {

    private val viewModel: TasksViewModel by viewModel()
    private val adapter by lazy { TasksAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.setText(R.string.title_tasks)
        setActionBar(bar)

        list_tasks.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        list_tasks.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        list_tasks.adapter = adapter

        viewModel.tasks.observe(this, Observer {
            adapter.submitList(it)
        })

    }
}