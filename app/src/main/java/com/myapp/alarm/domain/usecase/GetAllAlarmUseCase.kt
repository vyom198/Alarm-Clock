package com.myapp.alarm.domain.usecase

import com.myapp.alarm.data.model.Alarm
import com.myapp.alarm.domain.repo.AlarmRepo
import com.myapp.alarm.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import javax.inject.Inject

class GetAllAlarmUseCase@Inject constructor(
    private val alarmRepo: AlarmRepo
) {
      operator  fun invoke() : Flow<List<Alarm>>{
          return alarmRepo.getAllAlarms()
      }

}