package com.myapp.alarm.presentation

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myapp.alarm.data.model.Alarm
import com.myapp.alarm.domain.repo.AlarmRepo
import com.myapp.alarm.domain.usecase.GetAllAlarmUseCase
import com.myapp.alarm.domain.usecase.InsertAlarmUsecase
import com.myapp.alarm.domain.usecase.UpdateAlarmUsecase
import com.myapp.alarm.util.Constants
import com.myapp.alarm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@SuppressLint("NewApi")
@HiltViewModel
class AlarmViewmodel @Inject constructor(
    private  val getAllAlarmUseCase: GetAllAlarmUseCase,
    private val updateAlarmUsecase: UpdateAlarmUsecase,
    private val insertAlarmUsecase: InsertAlarmUsecase,
    private val alarmRepo: AlarmRepo
):ViewModel() {

    private  val  _eventflow : MutableSharedFlow<AlarmScreenEvent> = MutableSharedFlow()
    val eventflow = _eventflow.asSharedFlow()



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
            insertAlarmUsecase(alarm).let { resource ->
              when(resource){
                  is Resource.Success -> {
                     _eventflow.emit(AlarmScreenEvent.AlarmSetSuccessfully)
                  }
                  is Resource.Error -> {
                   when(resource.message){
                        Constants.NOTIFICATION_PERMISSION_NOT_AVAILABLE -> {
                            _eventflow.emit(AlarmScreenEvent.PermissionToSendNotificationsNotGranted)
                        }

                       Constants.REMINDER_PERMISSION_NOT_AVAILABLE -> {
                           _eventflow.emit(AlarmScreenEvent.PermissionToSetReminderNotGranted)
                       }
                       Constants.NOTIFICATION_REMINDER_PERMISSION_NOT_AVAILABLE-> {
                           _eventflow.emit(AlarmScreenEvent.PermissionsToSendNotificationsAndSetReminderNotGranted)
                       }
                       else-> {
                           _eventflow.emit(AlarmScreenEvent.AlarmNotSet(errorMessage = resource.message))
                       }
                   }

                  }
              }
            }
        }
    }

    fun updateAlarm (alarm: Alarm){
        viewModelScope.launch {
            updateAlarmUsecase(alarm = alarm).let {result->
                when(result){
                    is Resource.Success -> {
                        _eventflow.emit(AlarmScreenEvent.AlarmCancelled)
                    }
                    is Resource.Error ->{
                        _eventflow.emit(AlarmScreenEvent.AlarmNotCancelled(result.message.toString()))
                    }
                 }

            }
        }
    }


    fun deleteAlarm (alarm: Alarm){
        viewModelScope.launch {
            alarmRepo.deleteAlarm(alarm)
        }
    }



}


sealed class AlarmScreenEvent ( val message : String ? = null){
    object AlarmSetSuccessfully : AlarmScreenEvent()
    data class AlarmNotSet(val errorMessage: String?) : AlarmScreenEvent(errorMessage)
    object PermissionToSetReminderNotGranted : AlarmScreenEvent()
    object PermissionToSendNotificationsNotGranted : AlarmScreenEvent()
    object PermissionsToSendNotificationsAndSetReminderNotGranted :  AlarmScreenEvent()
    object AlarmCancelled : AlarmScreenEvent()
    data class AlarmNotCancelled(val errorMessage: String) :AlarmScreenEvent(errorMessage)
}
