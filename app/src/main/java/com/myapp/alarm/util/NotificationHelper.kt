package com.myapp.alarm.util

interface NotificationHelper {

    fun createNotificationChannel (
        name: String,
        id: String,
        importance: Int,
        vibratePattern: LongArray? = null

    )

    fun showNotification(
        channelId: String ,
       notificationId : Int,
        title : String,
        content : String
    )


}