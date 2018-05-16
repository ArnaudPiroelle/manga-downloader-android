package com.arnaudpiroelle.manga.service

import android.app.job.JobInfo
import android.app.job.JobInfo.NETWORK_TYPE_ANY
import android.app.job.JobInfo.NETWORK_TYPE_UNMETERED
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.core.db.HistoryDao
import com.arnaudpiroelle.manga.core.db.MangaDao
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.rx.plusAssign
import com.arnaudpiroelle.manga.core.utils.FileHelper
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper
import com.arnaudpiroelle.manga.model.db.History
import com.arnaudpiroelle.manga.model.db.Manga
import com.arnaudpiroelle.manga.model.network.Chapter
import com.arnaudpiroelle.manga.model.network.Page
import io.reactivex.Completable
import io.reactivex.Completable.fromAction
import io.reactivex.Observable.fromIterable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okio.Okio
import java.io.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.properties.Delegates


class DownloadService : JobService() {

    @Inject
    lateinit var providerRegistry: ProviderRegistry
    @Inject
    lateinit var fileHelper: FileHelper
    @Inject
    lateinit var mangaDao: MangaDao
    @Inject
    lateinit var historyDao: HistoryDao

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private var subscription = CompositeDisposable()
    private var notificationManager by Delegates.notNull<NotificationManager>()

    override fun onCreate() {
        super.onCreate()

        GRAPH.inject(this)
        notificationManager = NotificationManager(this)
    }

    override fun onStartJob(parameters: JobParameters?): Boolean {

        subscription += Completable.fromAction { historyDao.insertAll(History("Synchronisation")) }
                .subscribeOn(Schedulers.io())
                .subscribe({}, {})

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.initChannel()
        }
        notificationManager.startDownloadNotification()

        subscription += fromIterable(providerRegistry.list().entries)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::process)
                .subscribe({
                    println("Ended")
                    notificationManager.hideNotification()
                    jobFinished(parameters, false)
                }, {
                    notificationManager.hideNotification()
                    jobFinished(parameters, false)
                })

        return true
    }

    private fun process(provider: Map.Entry<String, MangaProvider>): Completable {
        return mangaDao.getMangaForProvider(provider.key)
                .firstElement()
                .flatMapCompletable {
                    fromIterable(it)
                            .doOnNext { println("Process ${it.name}") }
                            .flatMapCompletable { downloadManga(provider.value, it) }
                }

    }

    private fun downloadManga(provider: MangaProvider, manga: Manga): Completable {
        val chapters = provider.findChapters(manga)

        return chapters
                .flatMapObservable {
                    fromIterable(it)
                            .skip(alreadyDownloadedChapters(manga, it))
                }

                .map { ChapterInfo(manga, it) }
                .doOnNext { println("Download ${it.chapter.chapterNumber}") }
                .flatMapCompletable { chapterInfo ->
                    downloadChapter(provider, chapterInfo)
                            .andThen {
                                manga.lastChapter = chapterInfo.chapter.chapterNumber

                                mangaDao.update(manga)
                                it.onComplete()
                            }
                }
                .onErrorComplete()
    }

    private fun alreadyDownloadedChapters(manga: Manga, chapters: List<Chapter>): Long {
        if (manga.lastChapter == null || manga.lastChapter?.isEmpty()!!) {
            return (chapters.size - 1).toLong()
        } else {
            if ("all" == manga.lastChapter) {
                return 0
            } else {
                for (i in 0 until chapters.size) {
                    if (chapters[i].chapterNumber == manga.lastChapter) {
                        return (i + 1).toLong()
                    }
                }
            }
        }

        return 0
    }

    private fun downloadChapter(provider: MangaProvider, chapterInfo: ChapterInfo): Completable {
        val count = AtomicInteger(0)

        return provider.findPages(chapterInfo.manga, chapterInfo.chapter)
                .doOnSuccess { pages ->
                    val title = "${chapterInfo.manga.name} (${chapterInfo.chapter.chapterNumber})"
                    notificationManager.updateProgressNotification(title, 0, pages.size)
                }
                .flatMapCompletable { pages ->
                    fromIterable(pages)
                            .map { PageInfo(chapterInfo.manga, chapterInfo.chapter, it) }
                            .flatMapCompletable {
                                downloadPage(provider, it, pages.indexOf(it.page)).retry(3)
                                        .andThen {
                                            val title = "${chapterInfo.manga.name} (${chapterInfo.chapter.chapterNumber})"
                                            notificationManager.updateProgressNotification(title, count.incrementAndGet(), pages.size)
                                            it.onComplete()
                                        }
                            }

                }

                .andThen(compressChapter(chapterInfo))
                .andThen {
                    notificationManager.addToSummary(chapterInfo.manga)
                    historyDao.insertAll(History("${chapterInfo.manga.name} - ${chapterInfo.chapter.chapterNumber}"))
                    it.onComplete()
                }

    }

    private fun compressChapter(chapterInfo: ChapterInfo): Completable {

        return Completable.create {
            val manga = chapterInfo.manga
            val chapter = chapterInfo.chapter
            val mangaFolder = fileHelper.getMangaFolder(manga)

            val zipFile = File("%s/%s - %s.cbz".format(mangaFolder.absoluteFile, manga.name, chapter.chapterNumber))

            try {
                FileOutputStream(zipFile).use { dest ->
                    ZipOutputStream(BufferedOutputStream(dest)).use { out ->

                        zipFile.createNewFile()
                        val chapterFolder = fileHelper.getChapterFolder(manga, chapter)

                        val buffer = 1024
                        val data = ByteArray(buffer)

                        for (file in chapterFolder.listFiles()) {
                            FileInputStream(file).use { fi ->
                                BufferedInputStream(fi, buffer).use { origin ->

                                    val entry = ZipEntry(file.name)
                                    out.putNextEntry(entry)

                                    var count = origin.read(data, 0, buffer)
                                    while (count != -1) {
                                        out.write(data, 0, count)
                                        count = origin.read(data, 0, buffer)
                                    }
                                }
                            }
                        }

                        for (file in chapterFolder.listFiles()) {
                            file.delete()
                        }

                        chapterFolder.delete()

                    }
                }

                it.onComplete()
            } catch (e: IOException) {
                if (zipFile.exists()) {
                    zipFile.delete()
                }

                it.onError(e)
            }
        }
    }

    private fun downloadPage(provider: MangaProvider, pageInfo: PageInfo, pagePosition: Int): Completable {
        val pageFile = fileHelper.getPageFile(pageInfo.manga, pageInfo.chapter, pageInfo.page, pagePosition)

        return provider.findPage(pageInfo.page)
                .flatMapCompletable { response ->
                    fromAction {
                        val source = response.body()!!.source()
                        val sink = Okio.buffer(Okio.sink(pageFile))
                        sink.writeAll(source)
                        sink.close()

                        pageInfo.page.postProcess?.execute(pageFile)
                    }
                }
        //.doOnError { it.printStackTrace() }
    }

    override fun onStopJob(parameters: JobParameters?): Boolean {
        subscription.clear()
        return true
    }

    companion object {
        private const val JOB_ID = 1

        fun updateScheduling(context: Context, preferencesHelper: PreferencesHelper) {
            val updateInterval = preferencesHelper.updateInterval
            val isAutoUpdate = preferencesHelper.isAutoUpdate
            val updateOnWifiOnly = preferencesHelper.isUpdateOnWifiOnly

            if (isAutoUpdate) {
                updateScheduling(context, updateInterval.toLong() * 60 * 1000, updateOnWifiOnly)
            } else {
                cancelScheduling(context)
            }
        }

        fun updateScheduling(context: Context, period: Long = 60 * 1000, updateOnWifiOnly: Boolean = false) {
            val serviceComponent = ComponentName(context, DownloadService::class.java)
            val builder = JobInfo.Builder(JOB_ID, serviceComponent)
                    .setRequiredNetworkType(if (updateOnWifiOnly) NETWORK_TYPE_UNMETERED else NETWORK_TYPE_ANY)
                    .setPeriodic(period)
                    .setPersisted(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setRequiresStorageNotLow(true)
            }


            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler?
            jobScheduler?.schedule(builder.build())
        }

        private fun cancelScheduling(context: Context) {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler?
            jobScheduler?.cancel(JOB_ID)
        }
    }

    data class ChapterInfo(val manga: Manga, val chapter: Chapter)
    data class PageInfo(val manga: Manga, val chapter: Chapter, val page: Page)
}
