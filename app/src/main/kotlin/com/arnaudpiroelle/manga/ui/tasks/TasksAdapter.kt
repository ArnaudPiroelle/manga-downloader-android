package com.arnaudpiroelle.manga.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.calculateDiff
import com.arnaudpiroelle.manga.data.model.Task
import kotlinx.android.synthetic.main.item_view_task.view.*

class TasksAdapter(context: Context?) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    private val datas = mutableListOf<Task>()
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = layoutInflater.inflate(R.layout.item_view_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    fun update(tasks: List<Task>) {
        val calculateDiff = calculateDiff(datas, tasks) { old, new ->
            old.id == new.id
        }

        datas.clear()
        datas.addAll(tasks)
        calculateDiff.dispatchUpdatesTo(this)
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(task: Task) {
            itemView.task_name.text = task.status.name
        }
    }
}

