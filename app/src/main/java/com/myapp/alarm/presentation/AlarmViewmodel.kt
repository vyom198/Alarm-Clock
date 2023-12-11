package com.myapp.alarm.presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapp.alarm.data.model.Alarm
import com.myapp.alarm.domain.repo.AlarmRepo
import com.myapp.alarm.domain.usecase.GetAllAlarmUseCase
import com.myapp.alarm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
@SuppressLint("NewApi")
@HiltViewModel
class AlarmViewmodel @Inject constructor(
    private  val getAllAlarmUseCase: GetAllAlarmUseCase,
    private  val alarmRepo: AlarmRepo
):ViewModel() {

    private  val _AlarmState = MutableStateFlow(AlarmState())
    val alarmstate = _AlarmState.asStateFlow()


    var alarm by mutableStateOf(Alarm(id = 1, time = LocalTime.NOON,
        label = "anj",isScheduled = false,isVibrate = false))

    init {
        getAllAlarms()
    }


    private fun getAllAlarms () {
        viewModelScope.launch(Dispatchers.IO) {
           _AlarmState.update {
               it.copy(isLoading = false )
           }
           getAllAlarmUseCase().catch{throwrable->
               _AlarmState.update {
                   it.copy(error= throwrable.localizedMessage )
               }
           }.collect{alarmlist->
               _AlarmState.update {
                   it.copy( data = alarmlist)
               }
           }
        }
    }

    fun insertAlarm (alarm: Alarm){
        viewModelScope.launch {
            alarmRepo.inserAlarm(alarm)
        }
    }
    fun deleteAlarm (alarm: Alarm){
        viewModelScope.launch {
            alarmRepo.deleteAlarm(alarm)
        }
    }
    fun updateAlarm (alarm: Alarm){
        viewModelScope.launch {
            alarmRepo.updateAlarm(alarm)
        }
    }


}