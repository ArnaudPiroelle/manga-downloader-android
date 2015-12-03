package com.arnaudpiroelle.manga.core.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.util.*


class PermissionHelper(val permission: String) {
    fun permissionGranted(context: Context?, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun checkAndRequest(activity: Activity, requestCode: Int, success: () -> Unit) {
        if (!permissionGranted(activity, permission)) {
            ActivityCompat.requestPermissions(activity,
                    ArrayList<String>().apply { add(permission) }.toTypedArray(),
                    requestCode)
        } else {
            success.invoke()
        }
    }

}