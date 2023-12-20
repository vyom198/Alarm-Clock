package com.myapp.alarm.domain.usecase

import com.myapp.alarm.data.model.Alarm
import com.myapp.alarm.domain.repo.AlarmRepo
import com.myapp.alarm.util.AlarmScheduler
import com.myapp.alarm.util.Resource
import javax.inject.Inject

class UpdateAlarmUsecase@Inject constructor(
    private val alarmRepo: AlarmRepo,
    private  val alarmScheduler: AlarmScheduler,
) {
    suspend operator fun invoke(alarm : Alarm): Resource<Nothing?> {
        return try {
            if (alarm.isScheduled){
                alarmScheduler.setAlarm(alarm)

            }else{
                alarmScheduler.cancelAlarm(alarm)

            }
           alarmRepo.updateAlarm(alarm)

            Resource.Success(null)
        } catch (e: Exception) {
            Resource.Error(message = e.localizedMessage)
        }
    }


}