package com.myapp.alarm.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalTime
@RequiresApi(Build.VERSION_CODES.O)
class Converter {


        @TypeConverter
        fun fromLocalTime(value: LocalTime?): Long? {
            return value?.toNanoOfDay()
        }

        @TypeConverter
        fun toLocalTime(value: Long?): LocalTime? {
            return value?.let { LocalTime.ofNanoOfDay(it) }
        }

}