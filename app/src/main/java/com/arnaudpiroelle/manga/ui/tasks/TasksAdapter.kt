package com.arnaudpiroelle.manga.ui.tasks

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.utils.inflate
import com.arnaudpiroelle.manga.data.model.Task
import kotlinx.android.synthetic.main.item_view_task.view.*

class TasksAdapter : PagedListAdapter<Task, TasksAdapter.TaskViewHolder>(TaskDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = parent.inflate(R.layout.item_view_task, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val context = itemView.context
        fun bind(task: Task?) {

            if (task == null) {
                return
            }

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

object TaskDiff : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
}