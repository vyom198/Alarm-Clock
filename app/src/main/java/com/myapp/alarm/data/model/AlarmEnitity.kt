package com.myapp.alarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.myapp.alarm.data.local.Converter
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random

@Entity(tableName = "Alarm")
data class Alarm (
     @PrimaryKey(autoGenerate = true )
     val id : Int = 0,
     val time : LocalTime,
     val label : String? = null,
     val isScheduled : Boolean,
     val isVibrate : Boolean,

     )