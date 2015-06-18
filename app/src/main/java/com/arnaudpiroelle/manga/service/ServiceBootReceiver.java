package com.arnaudpiroelle.manga.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

public class ServiceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ServiceBootReceiver", "AlertManager " + intent.getAction());
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent service = new Intent(context, DownloadService.class);

            PendingIntent pendingIntent = PendingIntent.getService(context, 0, service, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setRepeating(
                    AlarmManager.RTC,
                    Calendar.getInstance().getTimeInMillis() + 600000,
                    600000,
                    pendingIntent);
        }
    }
}
