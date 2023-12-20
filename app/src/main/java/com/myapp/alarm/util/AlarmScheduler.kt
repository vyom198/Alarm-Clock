package com.myapp.alarm.util

import com.myapp.alarm.data.model.Alarm

interface AlarmScheduler {
    fun setAlarm(alarm: Alarm):SchedulerState
    fun cancelAlarm(alarm: Alarm )
    fun stopAlarm()
}


sealed class SchedulerState ( val error : String ? = null ){
    data class PermissionState (
        val alarmPermission : Boolean,
        val notificationPermission : Boolean
    ):SchedulerState()
    object SetSuccessfully : SchedulerState()
    data class NotSet(val message : String) : SchedulerState(error = message)

}