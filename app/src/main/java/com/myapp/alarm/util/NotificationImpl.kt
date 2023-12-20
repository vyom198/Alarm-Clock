package com.myapp.alarm.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.myapp.alarm.MainActivity
import com.myapp.alarm.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationImpl @Inject constructor(
   @ApplicationContext private val context: Context
) : NotificationHelper {
    @SuppressLint("NewApi")
    override fun createNotificationChannel(
        name: String,
        id: String,
        importance: Int,
        vibratePattern: LongArray?
    ) {
       val channel = NotificationChannel(
           id, name, importance
       )

        vibratePattern?.let {
          channel.enableVibration(true)
            channel.vibrationPattern =vibratePattern
            channel.enableLights(true)
        }

        val manager =context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        manager.createNotificationChannel(channel)

    }

    override fun createNotification(
        channelId: String,
        notificationId: Long,
        title: String,
        content: String,
        context: Context
    ):Notification {
        val alarmDismissPendingIntent =
            createAlarmDismissPendingIntent(context, pendingIntentId = notificationId)
        val intent = Intent(context,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent ,
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder = context?.let {
            NotificationCompat.Builder(it ,channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        }?.addAction(
             1,
            "Dismiss",
              alarmDismissPendingIntent,
        )?.build()

        return builder!!
    }




}