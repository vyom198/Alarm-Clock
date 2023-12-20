package com.myapp.alarm.services.broadcastReciever

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.WorkManager
import com.myapp.alarm.services.AlarmForegroundService
import com.myapp.alarm.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class AlarmBroadcastReciever  : BroadcastReceiver() {


    @SuppressLint("SuspiciousIndentation")
    override fun onReceive(context : Context?, intent : Intent?) {
        context ?: return
        intent ?: return
        val alarmServiceIntent = createAlarmServiceIntent(context, intent)

        when (intent.action) {
            Constants.ACTION_ALARM_FIRED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(alarmServiceIntent)
                    Log.d("Reciever", alarmServiceIntent.toString())
                } else {
                    context.startService(alarmServiceIntent)
                }
                // Set auto alarm end
                Timer().schedule(timerTask {
                    context.stopService(alarmServiceIntent)
                    sendBroadcastToCloseAlarmAlertActivity(context)
                }, Constants.ALARM_DURATION_MILLIS)

            }

            Constants.ACTION_ALARM_DISMISSED -> {
                // stopService doesn't send the intent. So, alarm dismiss is handled in AlarmService
                alarmServiceIntent.putExtra(Constants.EXTRA_IS_DISMISS, true)
                context.startService(alarmServiceIntent)
                sendBroadcastToCloseAlarmAlertActivity(context)
            }

        }
    }

    private fun createAlarmServiceIntent(context: Context, intent: Intent): Intent {
        return Intent(context, AlarmForegroundService::class.java).apply {
            putExtras(intent.extras!!)
        }
    }

    private fun sendBroadcastToCloseAlarmAlertActivity(context: Context) {
        val alarmAlertActivityCloseIntent =
            Intent(Constants.ACTION_ALARM_ALERT_ACTIVITY_CLOSE)
        context.sendBroadcast(alarmAlertActivityCloseIntent)
    }

}