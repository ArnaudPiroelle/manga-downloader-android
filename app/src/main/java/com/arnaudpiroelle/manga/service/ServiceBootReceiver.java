package com.arnaudpiroelle.manga.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ServiceBootReceiver", "AlertManager " + intent.getAction());
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            DownloadService.updateScheduling(context);
        }
    }
}
