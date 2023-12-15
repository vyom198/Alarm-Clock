package com.myapp.alarm.domain.usecase

import com.myapp.alarm.data.model.Alarm
import com.myapp.alarm.domain.repo.AlarmRepo
import com.myapp.alarm.presentation.AlarmState
import com.myapp.alarm.util.AlarmScheduler
import com.myapp.alarm.util.Constants
import com.myapp.alarm.util.Resource
import com.myapp.alarm.util.SchedulerState
import javax.inject.Inject

class InsertAlarmUsecase@Inject constructor(
    private val alarmRepo: AlarmRepo,
    private  val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke (alarm : Alarm): Resource<Nothing?> {
        return try {
            val  alarmstate = alarmScheduler.setAlarm(alarm)
            return when (alarmstate) {
                SchedulerState.SetSuccessfully -> {
                    alarmRepo.inserAlarm(alarm)
                    Resource.Success(null)
                }

                is SchedulerState.NotSet -> {
                    Resource.Error(message = alarmstate.error!!)
                }

                is SchedulerState.PermissionState -> {
                    when {
                        alarmstate.alarmPermission && !alarmstate.notificationPermission -> {
                            Resource.Error(
                                message = Constants.NOTIFICATION_PERMISSION_NOT_AVAILABLE
                            )
                        }

                        !alarmstate.alarmPermission&& alarmstate.notificationPermission -> {
                            Resource.Error(message = Constants.REMINDER_PERMISSION_NOT_AVAILABLE)
                        }

                        else -> {
                            Resource.Error(
                                message = Constants.NOTIFICATION_REMINDER_PERMISSION_NOT_AVAILABLE
                            )
                        }
                    }
                }
            }

        }catch (e:Exception){
            return Resource.Error(e.localizedMessage)
        }
    }

}