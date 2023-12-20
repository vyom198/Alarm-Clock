package com.myapp.alarm.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.myapp.alarm.data.local.Converter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.random.Random
import kotlin.random.nextULong

@Entity(tableName = "Alarm")
data class Alarm (
    @PrimaryKey(autoGenerate = true )
    val id :Long = Random.nextLong(0L,1000L) ,
    val time : LocalTime,
    val label : String? = null,
    val days  : List<Int>? = null,
    var isScheduled : Boolean,
    val isVibrate : Boolean,

    )
{
    /*
 Returns the LocalDateTime when the alarm should be first triggered.
  */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAlarmFirstTrigger(
        dayOfWeek: Int? = null, //  dayOfWeek is not null only for repeating alarms
    ): LocalDateTime {
        var alarmDateTime = LocalDateTime.of(LocalDate.now(), time)

        val nowDateTime = LocalDateTime.now()
        val nowDayOfWeek = nowDateTime.dayOfWeek.value
        val nowTime = nowDateTime.toLocalTime()

        val isTimeBefore = time < nowTime

        if (dayOfWeek != null) {

            val isRequiredDayBefore = dayOfWeek < nowDayOfWeek
            val isRequiredDaySame = dayOfWeek == nowDayOfWeek

            if (isRequiredDayBefore or (isRequiredDaySame and isTimeBefore)) {
                // forward it by 7 days
                val daysToAdd = 7 - (nowDayOfWeek - dayOfWeek)
                alarmDateTime = alarmDateTime.plusDays(daysToAdd.toLong())
            } else {
                // set day properly
                val daysToAdd = dayOfWeek - nowDayOfWeek
                alarmDateTime = alarmDateTime.plusDays(daysToAdd.toLong())
            }
        } else {
            if (isTimeBefore) {
                alarmDateTime = alarmDateTime.plusDays(1)
            }
        }
        return alarmDateTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAlarmFirstTriggerMillis(
        dayOfWeek: Int? = null,
    ): Long {
        return getAlarmFirstTrigger(dayOfWeek).atZone(ZoneId.systemDefault())
            .toEpochSecond() * 1000
    }



}