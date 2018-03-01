package com.arnaudpiroelle.manga.service

import android.app.job.JobInfo
import android.app.job.JobInfo.NETWORK_TYPE_ANY
import android.app.job.JobInfo.NETWORK_TYPE_UNMETERED
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.utils.FileHelper
import com.arnaudpiroelle.manga.core.utils.HttpUtils
import com.arnaudpiroelle.manga.model.Chapter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.model.Page
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingFragment
import io.reactivex.Completable
import io.reactivex.Observable.fromIterable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import se.emilsjolander.sprinkles.Query
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
    private var subscription = CompositeDisposable()
    private var notificationManager by Delegates.notNull<NotificationManager>()

    override fun onCreate() {
        super.onCreate()

        GRAPH.inject(this)
        notificationManager = NotificationManager(this)
    }

    override fun onStartJob(parameters: JobParameters?): Boolean {

        notificationManager.startDownloadNotification()

        val subscribe = fromIterable(providerRegistry.list())
                .flatMapCompletable(this::process)
                .subscribe({
                    println("Ended")
                    notificationManager.hideNotification()
                    jobFinished(parameters, false)
                }, {
                    notificationManager.hideNotification()
                    jobFinished(parameters, false)
                })

        subscription.add(subscribe)

        return true
    }

    private fun process(provider: MangaProvider): Completable {
        return fromIterable(Query.many(Manga::class.java, "select * from Mangas where provider=?", provider.name).get().asList())
                .subscribeOn(Schedulers.io())
                .doOnNext { println(it) }
                .flatMapCompletable { downloadManga(provider, it) }
    }

    private fun downloadManga(provider: MangaProvider, manga: Manga): Completable {
        val chapters = provider.findChapters(manga)
        manga.chapters = chapters

        return fromIterable(chapters)
                .skip(alreadyDownloadedChapters(manga))
                .map { ChapterInfo(manga, it) }
                .flatMapCompletable { chapterInfo ->
                    downloadChapter(provider, chapterInfo)
                            .andThen {
                                manga.lastChapter = chapterInfo.chapter.chapterNumber
                                manga.save()

                                sendBroadcast(Intent(MangaListingFragment.UPDATE_RECEIVER_ACTION))
                                it.onComplete()
                            }
                }
                .doOnError { it.printStackTrace() }
                .onErrorComplete()
    }

    private fun alreadyDownloadedChapters(manga: Manga): Long {
        if (manga.lastChapter == null || manga.lastChapter?.isEmpty()!!) {
            return (manga.chapters?.size!! - 1).toLong()
        } else {
            if ("all" == manga.lastChapter) {
                return 0
            } else {
                for (i in 0 until manga.chapters?.size!!) {
                    if (manga.chapters!![i].chapterNumber == manga.lastChapter) {
                        return (i + 1).toLong()
                    }
                }
            }
        }

        return 0
    }

    private fun downloadChapter(provider: MangaProvider, chapterInfo: ChapterInfo): Completable {
        val pages = provider.findPages(chapterInfo.chapter)
        chapterInfo.chapter.pages = pages

        val count = AtomicInteger(0)


        return fromIterable(pages)
                .map { PageInfo(chapterInfo.manga, chapterInfo.chapter, it) }
                .doOnNext {
                    val title = "${chapterInfo.manga.name} (${chapterInfo.chapter.chapterNumber})"
                    notificationManager.updateProgressNotification(title, count.getAndIncrement(), pages.size)
                }
                .flatMapCompletable { downloadPage(provider, it).retry(3) }
                .andThen(compressChapter(chapterInfo))
                .andThen {
                    notificationManager.addToSummary(chapterInfo.manga)
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

    private fun downloadPage(provider: MangaProvider, pageInfo: PageInfo): Completable {
        return Completable.create {
            try {
                val inputStream = provider.findPage(pageInfo.page)
                val pageFile = fileHelper.getPageFile(pageInfo.manga, pageInfo.chapter, pageInfo.page)

                HttpUtils.writeFile(inputStream, pageFile)

                it.onComplete()
            } catch (e: IOException) {
                it.onError(e)
            }
        }
    }

    override fun onStopJob(parameters: JobParameters?): Boolean {
        subscription.clear()
        return true
    }

    companion object {
        private const val JOB_ID = 1

        fun updateScheduling(context: Context, period: Long = 60 * 1000, updateOnWifiOnly: Boolean = false) {
            val serviceComponent = ComponentName(context, DownloadService::class.java)
            val builder = JobInfo.Builder(JOB_ID, serviceComponent)
                    .setRequiredNetworkType(if (updateOnWifiOnly) NETWORK_TYPE_UNMETERED else NETWORK_TYPE_ANY)
                    .setPeriodic(period)
                    .setPersisted(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setRequiresBatteryNotLow(true)
                        .setRequiresStorageNotLow(true)
            }


            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler?
            jobScheduler?.schedule(builder.build())
        }

        fun cancelScheduling(context: Context) {
            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler?
            jobScheduler?.cancel(JOB_ID)
        }
    }
}

data class ChapterInfo(val manga: Manga, val chapter: Chapter)
data class PageInfo(val manga: Manga, val chapter: Chapter, val page: Page)