package com.myapp.alarm.services.broadcastReciever

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.ui.unit.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.myapp.alarm.services.worker.NotificationCleanupWorker
import com.myapp.alarm.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class AlarmBroadcastReciever : BroadcastReceiver() {

 @Inject lateinit var workManager: WorkManager
    @SuppressLint("SuspiciousIndentation")
    override fun onReceive(p0: Context?, p1: Intent?) {
       val alarmLabel = p1?.getStringExtra(Constants.KEY_LAUNCH_NAME)
        val alarmId = p1?.getIntExtra(Constants.KEY_LAUNCH_ID,0)
        val alarmSchedule = p1?.getBooleanExtra(Constants.AlarmScheduled,false)

        val workRequest = OneTimeWorkRequestBuilder<NotificationCleanupWorker>()
            .setInputData(
                workDataOf(
                    Constants.KEY_LAUNCH_NAME to  alarmLabel,
                    Constants.KEY_LAUNCH_ID to alarmId,
                    Constants.AlarmScheduled to alarmSchedule


                )
            ).build()
            workManager.enqueueUniqueWork(
            Constants.ALARM_NOTIFICATION_AND_CLEANUP,
              ExistingWorkPolicy.APPEND_OR_REPLACE,
                 workRequest
            )


    }

}