package com.arnaudpiroelle.manga.core.manager

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.arnaudpiroelle.manga.service.CheckerService
import com.arnaudpiroelle.manga.service.TaskService

class Schedulers(
        private val context: Context,
        private val jobScheduler: JobScheduler) {


    fun scheduleTaskService() {
        val jobInfo = JobInfo.Builder(JOB_TASK, ComponentName(context, TaskService::class.java))
                .setPersisted(true)
                .setPeriodic(1 * 60 * 1000)
                .build()

        jobScheduler.schedule(jobInfo)
    }

    fun scheduleCheckerService() {
        val jobInfo = JobInfo.Builder(JOB_CHECKER, ComponentName(context, CheckerService::class.java))
                .setPersisted(true)
                .setPeriodic(1 * 60 * 1000)
                .build()

        jobScheduler.schedule(jobInfo)
    }

    companion object {
        const val JOB_TASK = 1
        const val JOB_CHECKER = 2
    }
}