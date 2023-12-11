package com.myapp.alarm.data.local

import android.annotation.SuppressLint
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myapp.alarm.data.model.Alarm

@SuppressLint("NewApi")
@Database(entities = [Alarm :: class] , version = 1 , exportSchema = false )
@TypeConverters(Converter::class)
 abstract class AlarmDatabase : RoomDatabase() {
    abstract fun getAlarmDao() : AlarmDao

}