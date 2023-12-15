package com.myapp.alarm.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.myapp.alarm.data.model.Alarm
import com.myapp.alarm.services.broadcastReciever.AlarmBroadcastReciever
import com.vanpra.composematerialdialogs.datetime.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    /** Apps targeting Android version 12 (Version Code - S, Api Level 31)
    // or higher need to request for SCHEDULE_EXACT_ALARM permission explicitly whereas
    // Apps targeting any Android version below 12 don't need to do ask for this permission

    // Apps targeting Android version 13 (Version Code - Tiramisu, Api Level 33)
    // or higher need to request for POST_NOTIFICATIONS permission explicitly whereas
    // Apps targeting any Android version below 13 don't need to do ask for this permission

    // 1. For 13 and above both SCHEDULE_EXACT_ALARM and POST_NOTIFICATIONS permission needs to be checked
    // 2. For 12, 12L SCHEDULE_EXACT_ALARM needs to checked
    // 3. For below 12 no neither needs to be checked
     **/
    val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun setAlarm(alarm: Alarm): SchedulerState {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return when {
                alarmManager.canScheduleExactAlarms() && areNotificationsEnabled(context)->{
                      return makeAlarm(alarm).let { error->
                          Log.d("scheduler", "setSuccessfully")
                          if (error.isNullOrBlank()){
                              SchedulerState.SetSuccessfully

                          }else{
                              SchedulerState.NotSet(message = error)
                          }
                      }

                }
                !alarmManager.canScheduleExactAlarms()&& areNotificationsEnabled(context)->{
                    SchedulerState.PermissionState(
                        alarmPermission = false,
                        notificationPermission = true
                    )
                }

                alarmManager.canScheduleExactAlarms()&& !areNotificationsEnabled(context)->{
                    SchedulerState.PermissionState(
                        alarmPermission = true,
                        notificationPermission = false
                    )
                }

                else -> {
                    SchedulerState.PermissionState(
                        alarmPermission = false,
                        notificationPermission = false
                    )
                }
            }

        }else if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.S){
           return  when {
               alarmManager.canScheduleExactAlarms() && areNotificationsEnabled(context) ->{
                  return makeAlarm(alarm).let {error->
                       if (error.isNullOrBlank()){
                           SchedulerState.SetSuccessfully
                       }else{
                           SchedulerState.NotSet(message = error)
                       }
                   }
               }

               else -> {
                   SchedulerState.PermissionState(
                       alarmPermission = false,
                       notificationPermission = true
                   )
               }
           }
        }else{
            return makeAlarm(alarm).let { errorMessage ->
                if (errorMessage.isNullOrBlank()) {
                    SchedulerState.SetSuccessfully
                } else {
                    SchedulerState.NotSet(message = errorMessage)
                }
            }
        }
    }

    override fun cancelAlarm(id: Int) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            Intent(context, AlarmBroadcastReciever::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(
            pendingIntent
        )
    }



    @SuppressLint("NewApi")
    private fun makeAlarm (alarm: Alarm) :String?{
        val intent = Intent(context,AlarmBroadcastReciever::class.java).apply {
            putExtra(Constants.KEY_LAUNCH_NAME, alarm.label)
            putExtra(Constants.KEY_LAUNCH_ID, alarm.id)
            putExtra(Constants.AlarmScheduled,alarm.isScheduled)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
         val alarmTime = alarm.time

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime.toNanoOfDay(),
            pendingIntent
        )
      return null
    }
    @SuppressLint("InlinedApi")
    private fun areNotificationsEnabled(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

}
