package com.myapp.alarm.data.repo

import com.myapp.alarm.data.local.AlarmDao
import com.myapp.alarm.data.model.Alarm
import com.myapp.alarm.domain.repo.AlarmRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmRepoImpl @Inject constructor(
    private  val alarmDao: AlarmDao
):AlarmRepo {
    override fun getAllAlarms(): Flow<List<Alarm>> {
        return  alarmDao.getAllAlarm()
    }

    override suspend fun inserAlarm(alarm:Alarm) {
        alarmDao.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm)
    }


}