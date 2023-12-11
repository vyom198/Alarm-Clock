package com.myapp.alarm.domain.usecase

import com.myapp.alarm.domain.repo.AlarmRepo
import javax.inject.Inject

class DeleteAlarmuseCase @Inject constructor(
    private val alarmRepo: AlarmRepo
) {

}