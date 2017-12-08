package com.arnaudpiroelle.manga.service

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.support.v7.app.NotificationCompat
import android.text.TextUtils
import android.util.Log
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.permission.PermissionHelper
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.utils.FileHelper
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.History
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page
import com.arnaudpiroelle.manga.service.MangaDownloadManager.MangaDownloaderCallback
import com.arnaudpiroelle.manga.ui.manga.NavigationActivity
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingFragment
import rx.Subscription
import rx.subscriptions.Subscriptions
import se.emilsjolander.sprinkles.Query
import java.util.*
import javax.inject.Inject


class DownloadService : Service(), MangaDownloaderCallback {

    @Inject lateinit var providerRegistry: ProviderRegistry
    @Inject lateinit var mNotifyManager: NotificationManager
    @Inject lateinit var mConnectivityManager: ConnectivityManager
    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var preferencesHelper: PreferencesHelper
    @Inject lateinit var fileHelper: FileHelper

    lateinit private var mangaDownloadManager: MangaDownloadManager
    lateinit private var permissionHelper: PermissionHelper

    private var mProgressNotificationBuilder: NotificationCompat.Builder? = null

    private var mDownloadNotificationBuilder: NotificationCompat.Builder? = null
    private var downloadCounter: MutableMap<String, MutableList<Chapter>>? = null
    private var pageIndex = 0

    private var running = false
    private var subscription: Subscription = Subscriptions.empty()

    override fun onCreate() {
        super.onCreate()

        GRAPH.inject(this);

        mangaDownloadManager = MangaDownloadManager(this, providerRegistry, fileHelper)
        permissionHelper = PermissionHelper(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)

        mNotifyManager.cancel(PROGRESS_NOTIFICATION_ID)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private var wakeLock: WakeLock? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag")
        wakeLock?.acquire()

        if (UPDATE_SCHEDULING == intent.action) {
            updateScheduling()
        } else {
            if (permissionHelper.permissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                startDownload(MANUAL_DOWNLOAD == intent.action)
            }
        }

        return Service.START_NOT_STICKY
    }

    private fun startDownload(manualDownload: Boolean) {
        val updateOnWifiOnly = preferencesHelper.isUpdateOnWifiOnly

        val mWifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (running || !manualDownload && !mWifi.isConnected && updateOnWifiOnly) {
            return
        }

        var history: History = History()
        history.date = Date().time
        history.label = getString(if (manualDownload) R.string.check_manual_manga_availability else R.string.check_manga_availability)

        history.save()

        running = true
        downloadCounter = HashMap<String, MutableList<Chapter>>()

        mProgressNotificationBuilder = NotificationCompat.Builder(this)
        mDownloadNotificationBuilder = NotificationCompat.Builder(this)

        mProgressNotificationBuilder!!.setContentTitle("Download started").setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher).setOngoing(true)

        mDownloadNotificationBuilder!!.setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher)

        mNotifyManager.notify(PROGRESS_NOTIFICATION_ID, mProgressNotificationBuilder!!.build())

        providerRegistry.list().forEach {
            val mangas = Query.many(Manga::class.java, "select * from Mangas where provider=?", it.name).get().asList()
            subscription = mangaDownloadManager.startDownload(it, mangas)
        }
    }

    override fun onDownloadError(throwable: Throwable) {
        Log.e("DownloadService", "Error on download", throwable)
        wakeLock?.release()
    }

    override fun onDownloadCompleted() {
        mProgressNotificationBuilder!!.setContentText("Download complete").setProgress(0, 0, false)

        mNotifyManager.notify(PROGRESS_NOTIFICATION_ID, mProgressNotificationBuilder!!.build())

        val refreshIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        refreshIntent.setData(Uri.parse("file://" + Environment.getExternalStorageDirectory()))
        sendBroadcast(refreshIntent)

        mNotifyManager.cancel(PROGRESS_NOTIFICATION_ID)

        stopSelf()
    }

    override fun onCompleteManga(manga: Manga) {

    }

    override fun onCompleteChapter(manga: Manga, chapter: Chapter) {

        if (preferencesHelper.isCompressChapter) {
            mangaDownloadManager.zipChapter(manga, chapter)
        }

        manga.lastChapter = chapter.chapterNumber
        manga.save()

        var history: History = History()
        history.date = Date().time
        history.label = manga.name + " " + chapter.chapterNumber + " downloaded"

        history.save()

        //eventBus.post(ChapterDownloadedEvent())
        sendBroadcast(Intent(MangaListingFragment.UPDATE_RECEIVER_ACTION))
        addToCounter(manga, chapter)

        var hasMultiChapter = false
        val issues = ArrayList<String>()
        for (key in downloadCounter!!.keys) {
            val size = downloadCounter!![key]!!.size
            val multiChapter = size > 1
            issues.add((if (multiChapter) ("$size ") else "") + key)

            hasMultiChapter = hasMultiChapter || multiChapter
        }

        hasMultiChapter = hasMultiChapter || downloadCounter!!.keys.size > 1

        val title = "New chapter${if (hasMultiChapter) "s" else ""} downloaded"
        mDownloadNotificationBuilder!!.setContentTitle(title).setContentText(TextUtils.join(", ", issues)).setSmallIcon(R.mipmap.ic_launcher)

        mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, mDownloadNotificationBuilder!!.build())


        pageIndex = 0
    }

    override fun onCompletePage(manga: Manga, chapter: Chapter, page: Page) {

        mProgressNotificationBuilder!!.setContentTitle(manga.name + " (" + chapter.chapterNumber + ")").setContentText("Download in progress").setProgress(chapter.pages?.size!!, ++pageIndex, false)
        mNotifyManager.notify(PROGRESS_NOTIFICATION_ID, mProgressNotificationBuilder!!.build())

    }

    private fun addToCounter(manga: Manga, chapter: Chapter) {
        var chapters: MutableList<Chapter>? = downloadCounter!![manga.name]

        if (chapters == null) {
            chapters = ArrayList<Chapter>()
        }

        chapters.add(chapter)

        downloadCounter!!.put(manga.name!!, chapters)
    }

    private val pendingIntent: PendingIntent
        get() = PendingIntent.getActivity(
                this,
                0,
                Intent(this, NavigationActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)

    fun updateScheduling() {
        val service = Intent(this, DownloadService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, service, 0)

        val existingPendingIntent = PendingIntent.getService(this, 0, service, PendingIntent.FLAG_NO_CREATE)
        if (existingPendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }

        if (preferencesHelper.isAutoUpdate) {
            val interval = java.lang.Long.parseLong(preferencesHelper.updateInterval) * 60 * 1000

            alarmManager.setRepeating(
                    AlarmManager.RTC,
                    Calendar.getInstance().timeInMillis + interval,
                    interval,
                    pendingIntent)
        }
    }

    companion object {

        val UPDATE_SCHEDULING = "UPDATE_SCHEDULING"
        val MANUAL_DOWNLOAD = "MANUAL_DOWNLOAD"
        private val PROGRESS_NOTIFICATION_ID = 1234567890
        private val DOWNLOAD_NOTIFICATION_ID = 987654321

        fun updateScheduling(context: Context) {
            val serviceIntent = Intent(context, DownloadService::class.java)
            serviceIntent.setAction(DownloadService.UPDATE_SCHEDULING)

            context.startService(serviceIntent)
        }
    }
}
