package com.myapp.alarm.presentation

import com.myapp.alarm.data.model.Alarm

data class AlarmState (
  val isLoading : Boolean = false ,
  val data : List<Alarm> = emptyList() ,
    val error : String? = null
)
