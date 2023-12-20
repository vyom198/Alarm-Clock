package com.myapp.alarm.services

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.work.WorkManager
import com.myapp.alarm.util.AlarmScheduler
import com.myapp.alarm.util.NotificationHelper
import com.myapp.alarm.util.NotificationImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent ::class)
abstract class ServiceModule {


    companion object {

        @Provides
        @Singleton
        fun provideReminderScheduler(@ApplicationContext context: Context): AlarmScheduler {
            return AlarmSchedulerImpl(context)
        }

        @Provides
        @Singleton
        fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @SuppressLint("NewApi")
        @Provides
        fun provideMediaPlayer(@ApplicationContext context: Context): MediaPlayer =
            MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)

        @Singleton
        @Provides
        fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
            WorkManager.getInstance(context)

        @Provides
        @Singleton
        fun provideVibrator(app: Application): Vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (app.getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                    .defaultVibrator
            } else {
                app.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
            }

        @Provides
        @Singleton
        fun provideNotificationManager(app: Application): NotificationManager {
            return app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }

    }

    @Binds
    abstract fun bindNotificationHelper (notificationImpl: NotificationImpl):NotificationHelper




}
