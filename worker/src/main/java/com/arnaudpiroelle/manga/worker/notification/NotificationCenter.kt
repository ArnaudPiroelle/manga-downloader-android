package com.arnaudpiroelle.manga.worker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.arnaudpiroelle.manga.data.model.Chapter
import com.arnaudpiroelle.manga.data.model.Chapter.Status.DOWNLOADED
import com.arnaudpiroelle.manga.worker.R
import com.arnaudpiroelle.manga.worker.notification.NotificationCenter.Notification.*

class NotificationCenter(
        private val context: Context,
        private val notificationManager: NotificationManager,
        private val notificationManagerCompat: NotificationManagerCompat) {

    sealed class Notification {
        object SyncStarted : Notification()
        data class SyncProgress(val progress: Int, val total: Int, val name: String) : Notification()
        object SyncEnded : Notification()
        data class DownloadStarted(val name: String) : Notification()
        data class DownloadProgress(val progress: Int, val total: Int, val name: String) : Notification()
        data class DownloadCompressStarted(val name: String) : Notification()
        data class DownloadCompressProgress(val progress: Int, val total: Int, val name: String) : Notification()
        data class DownloadEnded(val id: Long, val name: String, val number: String, val status: Chapter.Status) : Notification()

    }

    fun notify(notification: Notification) {
        when (notification) {
            is SyncStarted -> notifySyncStarted()
            is SyncProgress -> notifySyncProgress(notification.progress, notification.total, notification.name)
            is SyncEnded -> notifySyncEnded()
            is DownloadStarted -> notifyDownloadStarted(notification.name)
            is DownloadProgress -> notifyDownloadProgress(notification.name, notification.progress, notification.total)
            is DownloadCompressStarted -> notifyDownloadCompressStarted(notification.name)
            is DownloadCompressProgress -> notifyDownloadCompressProgress(notification.name, notification.progress, notification.total)
            is DownloadEnded -> notifyDownloadEnded(notification.id, notification.name, notification.number, notification.status)
        }
    }

    private fun notifyDownloadEnded(id: Long, mangaName: String, chapterName: String, status: Chapter.Status) {
        val summaryNotification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SYNC)
                .setContentTitle("Group")
                .setContentText("New mangas")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setStyle(NotificationCompat.InboxStyle())
                .setGroup("NewDownloads")
                .setGroupSummary(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

        val notification = when (status) {
            DOWNLOADED -> NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SYNC)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle(mangaName)
                    .setSubText("New chapter available")
                    .setContentText(chapterName)
                    .setGroup("NewDownloads")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()
            else -> NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SYNC)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle(mangaName)
                    .setSubText("Download failed")
                    .setContentText(chapterName)
                    .setGroup("NewDownloads")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build()
        }


        notificationManagerCompat.cancel(NOTIFICATION_PROGRESS_ID)
        notificationManagerCompat.notify(id.toInt() * NOTIFICATION_PADDING_ID, notification)
        notificationManagerCompat.notify(NOTIFICATION_DOWNLOAD_SUMMARY_ID, summaryNotification)
    }

    private fun notifyDownloadCompressProgress(name: String, progress: Int, total: Int) {
        val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SYNC)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(name)
                .setContentText("Compressing")
                .setProgress(total, progress, false)
                .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManagerCompat.notify(NOTIFICATION_PROGRESS_ID, mBuilder.build())
    }

    private fun notifyDownloadCompressStarted(name: String) {
        val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SYNC)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(name)
                .setContentText("Compressing")
                .setProgress(0, 0, true)
                .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManagerCompat.notify(NOTIFICATION_PROGRESS_ID, mBuilder.build())
    }

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

    private fun notifySyncStarted() {
        val notificationSyncBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SYNC)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle("Synchronization started")
                .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManagerCompat.notify(NOTIFICATION_SYNC_ID, notificationSyncBuilder.build())
    }

    private fun notifySyncProgress(progress: Int, total: Int, name: String) {
        val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SYNC)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentText(name)
                .setContentTitle("Check for updates")
                .setProgress(total, progress, false)
                .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManagerCompat.notify(NOTIFICATION_SYNC_ID, mBuilder.build())
    }

    private fun notifySyncEnded() {
        notificationManagerCompat.cancel(NOTIFICATION_SYNC_ID)
    }

    private fun notifyDownloadStarted(name: String) {
        val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SYNC)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(name)
                .setContentText("Starting")
                .setProgress(0, 0, true)
                .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManagerCompat.notify(NOTIFICATION_PROGRESS_ID, mBuilder.build())
    }

    private fun notifyDownloadProgress(name: String, progress: Int, total: Int) {
        val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_SYNC)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(name)
                .setContentText("Downloading")
                .setProgress(total, progress, false)
                .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManagerCompat.notify(NOTIFICATION_PROGRESS_ID, mBuilder.build())
    }

    companion object {
        const val NOTIFICATION_CHANNEL_SYNC = "NOTIFICATION_CHANNEL_SYNC"

        const val NOTIFICATION_SYNC_ID = 0
        const val NOTIFICATION_PROGRESS_ID = 1
        const val NOTIFICATION_DOWNLOAD_SUMMARY_ID = 2
        const val NOTIFICATION_PADDING_ID = 10
    }
}