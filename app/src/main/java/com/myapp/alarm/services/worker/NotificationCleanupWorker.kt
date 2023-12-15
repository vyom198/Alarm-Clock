package com.myapp.alarm.services.worker

import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.myapp.alarm.data.local.AlarmDao
import com.myapp.alarm.data.model.Alarm
import com.myapp.alarm.util.Constants
import com.myapp.alarm.util.NotificationHelper
import com.myapp.alarm.util.pickedTimeformat
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

@HiltWorker
class NotificationCleanupWorker @AssistedInject constructor(
    @Assisted appContext : Context,
    @Assisted parameters: WorkerParameters,
    private val mediaPlayer: MediaPlayer,
    private val notificationHelper: NotificationHelper,
    private val alarmDao: AlarmDao
) : CoroutineWorker(appContext,parameters) {


    override suspend fun doWork(): Result {
      return  try {
           val alarmName = inputData.getString(Constants.KEY_LAUNCH_NAME)
          val alarmScheduled = inputData.getBoolean(Constants.AlarmScheduled,false)
          val alarmId = inputData.getInt( Constants.KEY_LAUNCH_ID,0)
          withContext(Dispatchers.Main){
                mediaPlayer.start()
                showNotification(alarmName)
                 delay(Constants.REMINDER_SOUND_DURATION)
                mediaPlayer.stop()
              alarmDao.updateSchedule(alarmScheduled,alarmId)

          }
           Result.success()

      }catch (e:Exception){
          e.printStackTrace()
          withContext(Dispatchers.Main){
              mediaPlayer.stop()
          }
          Result.failure()
      }


    }

    private fun showNotification(alarmLabel: String?) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            notificationHelper.createNotificationChannel(
                name = Constants.NOTIFICATION_CHANNEL,
                id = Constants.CHANNEL_ID,
                importance = NotificationManager.IMPORTANCE_HIGH
            )
            notificationHelper.showNotification(
                channelId = Constants.CHANNEL_ID,
                notificationId = Random.nextInt(),
                title = alarmLabel.toString(),
                content = "Alarm Is Ringing "
            )
        }
    }

}