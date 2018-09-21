package com.arnaudpiroelle.manga

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobInfo.NETWORK_TYPE_ANY
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.arnaudpiroelle.manga.api.Mangas
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.provider.japscan.JapScan
import com.arnaudpiroelle.manga.service.TaskService
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class MangaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Timber.plant(Timber.DebugTree())
        } else {
            Fabric.with(this, Crashlytics())
        }

        Mangas.with(this, JapScan())

        inject()

        scheduleTaskService()

    }

    private fun scheduleTaskService() {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val jobInfo = JobInfo.Builder(JOB_TASK, ComponentName(this, TaskService::class.java))
                .setPersisted(true)
                .setRequiredNetworkType(NETWORK_TYPE_ANY)
                .build()

        jobScheduler.schedule(jobInfo)
    }

    companion object {
        const val JOB_TASK = 1
    }

}
