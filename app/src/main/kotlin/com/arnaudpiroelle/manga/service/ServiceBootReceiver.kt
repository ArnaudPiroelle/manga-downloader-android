package com.arnaudpiroelle.manga.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ServiceBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("ServiceBootReceiver", "AlertManager " + intent.action)

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            DownloadService.updateScheduling(context)
        }
    }
}
