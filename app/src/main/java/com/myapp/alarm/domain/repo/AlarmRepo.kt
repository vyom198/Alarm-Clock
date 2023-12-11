package com.myapp.alarm.domain.repo

import com.myapp.alarm.data.local.AlarmDao
import com.myapp.alarm.data.model.Alarm
import kotlinx.coroutines.flow.Flow


interface AlarmRepo  {

    fun getAllAlarms () : Flow<List<Alarm>>

   suspend fun inserAlarm (alarm: Alarm)

    suspend fun deleteAlarm (alarm: Alarm)

     suspend fun updateAlarm ( alarm: Alarm)

}