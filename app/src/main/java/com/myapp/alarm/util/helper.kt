package com.myapp.alarm.util

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.myapp.alarm.services.broadcastReciever.AlarmBroadcastReciever
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun pickedTimeformat(time: LocalTime):String{
    return DateTimeFormatter
        .ofPattern("hh:mm a")
        .format(time)
}
fun Activity.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", this.packageName, null)
    )
    this.startActivity(intent)
}
fun createAlarmDismissPendingIntent(
    applicationContext: Context,
    pendingIntentId: Long
): PendingIntent {
    val alarmDismissIntent = createAlarmDismissIntent(applicationContext, pendingIntentId)
    return PendingIntent.getBroadcast(
        applicationContext,
        pendingIntentId.toInt(),
        alarmDismissIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

fun createAlarmDismissIntent(
    applicationContext: Context,
    notificationId: Long,
    isDismiss: Boolean = false
): Intent {
    return Intent(Constants.ACTION_ALARM_DISMISSED).apply {
        setClass(applicationContext, AlarmBroadcastReciever::class.java)
        putExtra(Constants.EXTRA_NOTIFICATION_ID, notificationId)
        putExtra(Constants.EXTRA_IS_DISMISS, isDismiss)
    }
}
