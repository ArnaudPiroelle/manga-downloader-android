package com.arnaudpiroelle.manga.ui.tasks

import com.arnaudpiroelle.manga.data.model.Task

interface TasksContract {
    interface View {
        fun displayTasks(tasks: List<Task>)
    }

    interface UserActionsListener {
        fun retrieveTasks()
        fun unregister()
    }
}