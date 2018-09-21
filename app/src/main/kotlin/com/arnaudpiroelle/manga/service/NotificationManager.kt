package com.arnaudpiroelle.manga.service

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.data.model.Manga


class NotificationManager(val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val summary = HashMap<Manga, Int>()

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
        val currentCount = summary.getOrElse(add) { 0 }
        summary[add] = currentCount + 1

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
            //notificationManager.notify(manga.mangaAlias, SUMMARY_NOTIF_ID, newMessageNotification)

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun initChannel() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        val channelName = "Downloads"
        val importance = android.app.NotificationManager.IMPORTANCE_LOW
        val notificationChannel = NotificationChannel(CHANNEL_ID, channelName, importance)
        notificationManager.createNotificationChannel(notificationChannel)

    }

    companion object {
        const val CHANNEL_ID = "DOWNLOAD_CHANNEL"
        const val PROGESS_NOTIF_ID = 1
        const val SUMMARY_NOTIF_ID = 1
        const val GROUP_ID = "DOWNLOAD_SUMMARY"
    }
}