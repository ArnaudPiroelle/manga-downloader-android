package com.arnaudpiroelle.manga.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry;
import com.arnaudpiroelle.manga.core.utils.PreferencesHelper;
import com.arnaudpiroelle.manga.event.ChapterDownloadedEvent;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.History;
import com.arnaudpiroelle.manga.model.Manga;
import com.arnaudpiroelle.manga.model.Page;
import com.arnaudpiroelle.manga.service.MangaDownloadManager.MangaDownloaderCallback;
import com.arnaudpiroelle.manga.ui.manga.NavigationActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.Query;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;
import static com.arnaudpiroelle.manga.model.History.HistoryBuilder.createHisotry;

public class DownloadService extends Service implements MangaDownloaderCallback {

    public static final String UPDATE_SCHEDULING = "UPDATE_SCHEDULING";
    public static final String MANUAL_DOWNLOAD = "MANUAL_DOWNLOAD";
    private static final int PROGRESS_NOTIFICATION_ID = 1234567890;
    private static final int DOWNLOAD_NOTIFICATION_ID = 987654321;
    @Inject
    ProviderRegistry providerRegistry;

    @Inject
    EventBus eventBus;

    @Inject
    NotificationManager mNotifyManager;

    @Inject
    ConnectivityManager mConnectivityManager;

    @Inject
    AlarmManager alarmManager;

    @Inject
    PreferencesHelper preferencesHelper;

    private MangaDownloadManager mangaDownloadManager;

    private NotificationCompat.Builder mProgressNotificationBuilder = null;
    private NotificationCompat.Builder mDownloadNotificationBuilder = null;

    private Map<String, List<Chapter>> downloadCounter;
    private int pageIndex = 0;
    private boolean running = false;

    public static void updateScheduling(Context context) {
        Intent serviceIntent = new Intent(context, DownloadService.class);
        serviceIntent.setAction(DownloadService.UPDATE_SCHEDULING);

        context.startService(serviceIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        GRAPH.inject(this);

        mangaDownloadManager = new MangaDownloadManager(this, providerRegistry);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        mNotifyManager.cancel(PROGRESS_NOTIFICATION_ID);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (UPDATE_SCHEDULING.equals(intent.getAction())) {
            updateScheduling();
        } else {
            startDownload(MANUAL_DOWNLOAD.equals(intent.getAction()));
        }

        return START_NOT_STICKY;
    }

    private void startDownload(boolean manualDownload) {
        boolean updateOnWifiOnly = preferencesHelper.isUpdateOnWifiOnly();

        NetworkInfo mWifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (running || !manualDownload && !mWifi.isConnected() && updateOnWifiOnly) {
            return;
        }

        History.HistoryBuilder.createHisotry()
                .withDate(new Date())
                .withLabel(getString(manualDownload ?
                        R.string.check_manual_manga_availability :
                        R.string.check_manga_availability))
                .build()
                .save();

        running = true;
        downloadCounter = new HashMap<>();

        mProgressNotificationBuilder = new NotificationCompat.Builder(this);
        mDownloadNotificationBuilder = new NotificationCompat.Builder(this);

        mProgressNotificationBuilder
                .setContentTitle("Download started")
                .setContentIntent(getPendingIntent())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true);

        mDownloadNotificationBuilder
                .setContentIntent(getPendingIntent())
                .setSmallIcon(R.mipmap.ic_launcher);

        mNotifyManager.notify(PROGRESS_NOTIFICATION_ID, mProgressNotificationBuilder.build());

        try (CursorList<Manga> cursorList = Query.all(Manga.class).get()) {
            List<Manga> mangas = cursorList.asList();
            mangaDownloadManager.startDownload(mangas);
        }
    }

    @Override
    public void onDownloadError(Throwable throwable) {
        Log.e("DownloadService", "Error on download", throwable);

        mNotifyManager.cancel(PROGRESS_NOTIFICATION_ID);

        stopSelf();
    }

    @Override
    public void onDownloadCompleted() {
        mProgressNotificationBuilder
                .setContentText("Download complete")
                .setProgress(0, 0, false);

        mNotifyManager.notify(PROGRESS_NOTIFICATION_ID, mProgressNotificationBuilder.build());

        Intent refreshIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        refreshIntent.setData(Uri.parse("file://" + Environment.getExternalStorageDirectory()));
        sendBroadcast(refreshIntent);

        mNotifyManager.cancel(PROGRESS_NOTIFICATION_ID);

        stopSelf();
    }

    @Override
    public void onCompleteManga(Manga manga) {

    }

    @Override
    public void onCompleteChapter(Manga manga, Chapter chapter) {

        if (preferencesHelper.isCompressChapter()) {
            mangaDownloadManager.zipChapter(manga, chapter);
        }

        manga.setLastChapter(chapter.getChapterNumber());
        manga.save();

        createHisotry()
                .withDate(new Date())
                .withLabel(manga.getName() + " " + chapter.getChapterNumber() + " downloaded")
                .build()
                .save();

        eventBus.post(new ChapterDownloadedEvent());
        addToCounter(manga, chapter);

        boolean hasMultiChapter = false;
        List<String> issues = new ArrayList<>();
        for (String key : downloadCounter.keySet()) {
            int size = downloadCounter.get(key).size();
            boolean multiChapter = size > 1;
            issues.add((multiChapter ? size + " " : "") + key);

            hasMultiChapter = hasMultiChapter || multiChapter;
        }

        hasMultiChapter = hasMultiChapter || downloadCounter.keySet().size() > 1;

        String title = "New chapter" + (hasMultiChapter ? "s" : "") + " downloaded";
        mDownloadNotificationBuilder.setContentTitle(title)
                .setContentText(TextUtils.join(", ", issues))
                .setSmallIcon(R.mipmap.ic_launcher);

        mNotifyManager.notify(DOWNLOAD_NOTIFICATION_ID, mDownloadNotificationBuilder.build());


        pageIndex = 0;
    }

    @Override
    public void onCompletePage(Manga manga, Chapter chapter, Page page) {

        mProgressNotificationBuilder
                .setContentTitle(manga.getName() + " (" + chapter.getChapterNumber() + ")")
                .setContentText("Download in progress")
                .setProgress(chapter.getPages().size(), ++pageIndex, false);
        mNotifyManager.notify(PROGRESS_NOTIFICATION_ID, mProgressNotificationBuilder.build());

    }

    private void addToCounter(Manga manga, Chapter chapter) {
        List<Chapter> chapters = downloadCounter.get(manga.getName());

        if (chapters == null) {
            chapters = new ArrayList<>();
        }

        chapters.add(chapter);

        downloadCounter.put(manga.getName(), chapters);
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(
                this,
                0,
                new Intent(this, NavigationActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    public void updateScheduling() {
        Intent service = new Intent(this, DownloadService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, service, 0);

        PendingIntent existingPendingIntent = PendingIntent.getService(this, 0, service, PendingIntent.FLAG_NO_CREATE);
        if (existingPendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }

        if (preferencesHelper.isAutoUpdate()) {
            long interval = Long.parseLong(preferencesHelper.getUpdateInterval()) * 60 * 1000;

            alarmManager.setRepeating(
                    AlarmManager.RTC,
                    Calendar.getInstance().getTimeInMillis() + interval,
                    interval,
                    pendingIntent);
        }
    }
}
