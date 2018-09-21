package com.arnaudpiroelle.manga.ui.tasks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.core.inject.uninject
import com.arnaudpiroelle.manga.data.TaskRepository
import com.arnaudpiroelle.manga.data.model.Task
import kotlinx.android.synthetic.main.fragment_tasks.*
import javax.inject.Inject

class TasksFragment : Fragment(), TasksContract.View {

    @Inject
    lateinit var taskRepository: TaskRepository

    private val userActionsListener by lazy { TasksPresenter(this, taskRepository) }
    private val adapter by lazy { TasksAdapter(activity) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        inject()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setTitle(R.string.title_tasks)

        list_tasks.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        list_tasks.adapter = adapter

    }

    override fun onResume() {
        super.onResume()

        userActionsListener.retrieveTasks()
    }

    override fun onPause() {
        userActionsListener.unregister()

        super.onPause()
    }

    override fun onDetach() {
        uninject()

        super.onDetach()
    }

    override fun displayTasks(tasks: List<Task>) {
        adapter.update(tasks)
    }
}