package com.myapp.alarm.util

import android.app.Notification
import android.content.Context

interface NotificationHelper {

    fun createNotificationChannel (
        name: String,
        id: String,
        importance: Int,
        vibratePattern: LongArray? = null

    )

    fun createNotification(
        channelId: String,
        notificationId: Long,
        title: String,
        content: String,
        context: Context
    ): Notification


}