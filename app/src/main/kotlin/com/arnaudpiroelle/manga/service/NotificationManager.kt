package com.arnaudpiroelle.manga.service

import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.model.Manga


class NotificationManager(val context: Context) {

    val notificationManager = NotificationManagerCompat.from(context)
    val summary = HashMap<Manga, Int>()

    fun hideNotification() {
        notificationManager.cancel(PROGESS_NOTIF_ID)
    }

    fun startDownloadNotification() {
        summary.clear()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Synchronization started")
                .build()
        notificationManager.notify(PROGESS_NOTIF_ID, notification)
    }

    fun updateProgressNotification(title: String, progress: Int, max: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("Download in progress")
                .setProgress(max, progress, false)
                .build()

        notificationManager.notify(PROGESS_NOTIF_ID, notification)
    }

    fun addToSummary(add: Manga) {
        val count = summary.getOrElse(add) { 0 }
        summary[add] = count + 1

        val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle("${summary.count()} manga updated")
                .setSummaryText("${summary.count()} manga updated")

        summary.forEach { (manga, count) ->
            val newMessageNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(manga.name)
                    .setContentText("$count new chapter(s) downloaded")
                    .setGroup(GROUP_ID)
                    .build()
            notificationManager.notify(manga.mangaAlias, SUMMARY_NOTIF_ID, newMessageNotification)

            inboxStyle.addLine("${manga.name}: $count new chapter(s)")
        }

        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Download summary")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(inboxStyle)
                .setGroup(GROUP_ID)
                .setGroupSummary(true)
                .build()
        notificationManager.notify("summary", SUMMARY_NOTIF_ID, summaryNotification)
    }

    companion object {
        const val CHANNEL_ID = "DOWNLOAD_CHANNEL"
        const val PROGESS_NOTIF_ID = 1
        const val SUMMARY_NOTIF_ID = 1
        const val GROUP_ID = "DOWNLOAD_SUMMARY"
    }
}