package com.myapp.alarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.myapp.alarm.util.Constants
import com.myapp.alarm.util.NotificationHelper
import com.myapp.alarm.util.NotificationImpl
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication() : Application() {

    @Inject lateinit var notificationHelper: NotificationHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannel(
            id = Constants.CHANNEL_ID,
            name  = Constants.NOTIFICATION_CHANNEL,
            importance = NotificationManager.IMPORTANCE_HIGH,
            )
    }

}