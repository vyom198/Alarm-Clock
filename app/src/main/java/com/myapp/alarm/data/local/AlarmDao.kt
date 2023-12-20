package com.myapp.alarm.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.myapp.alarm.data.model.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("Select * from Alarm ")
    fun getAllAlarm () : Flow<List<Alarm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertAlarm (alarm: Alarm)

     @Delete
      suspend fun deleteAlarm (alarm: Alarm)

      @Update(onConflict = OnConflictStrategy.REPLACE)
        suspend fun  updateAlarm ( alarm: Alarm)

       @Query("Update Alarm set isScheduled = :bool where id = :id")
         suspend fun updateSchedule(bool : Boolean, id : Long)

}