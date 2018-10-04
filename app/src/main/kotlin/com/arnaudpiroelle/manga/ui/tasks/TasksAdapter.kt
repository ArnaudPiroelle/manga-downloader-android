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
        val context = itemView.context
        fun bind(task: Task) {
            itemView.task_name.text = task.label
            itemView.task_desc.text = when (task.type) {
                Task.Type.RETRIEVE_CHAPTERS -> context.getString(R.string.task_retrieve_chapters)
                Task.Type.DOWNLOAD_CHAPTER -> context.getString(R.string.task_download_chapter)
            }
            val color = when (task.status) {
                Task.Status.NEW -> context.getColor(R.color.task_status_new)
                Task.Status.IN_PROGRESS -> context.getColor(R.color.task_status_in_progress)
                Task.Status.SUCCESS -> context.getColor(R.color.task_status_done)
                Task.Status.ERROR -> context.getColor(R.color.task_status_error)
            }
            itemView.task_status.setBackgroundColor(color)
        }
    }
}

