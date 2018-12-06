package com.arnaudpiroelle.manga.worker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.arnaudpiroelle.manga.worker.R

class NotificationCenter(
        private val context: Context,
        private val notificationManager: NotificationManager,
        private val notificationManagerCompat: NotificationManagerCompat) {

    fun createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channelName = context.getString(R.string.notification_channel_sync_name)
            val channelDescription = context.getString(R.string.notification_channel_sync_description)

            val channel = NotificationChannel(NOTIFICATION_CHANNEL_SYNC, channelName, importance)
            channel.description = channelDescription
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun notifySynchronizationStarted() {

    }

    companion object {
        const val NOTIFICATION_CHANNEL_SYNC = "NOTIFICATION_CHANNEL_SYNC"
    }
}