package com.myapp.alarm.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.ParameterizedType
import java.time.LocalTime
@RequiresApi(Build.VERSION_CODES.O)
class Converter {

    private val moshi = Moshi.Builder().build()
    private val type: ParameterizedType =
        Types.newParameterizedType(List::class.java, Int::class.javaObjectType)
    private val jsonAdapter: JsonAdapter<List<Int>> = moshi.adapter<List<Int>>(type)

        @TypeConverter
        fun fromLocalTime(value: LocalTime?): Long? {
            return value?.toNanoOfDay()
        }

        @TypeConverter
        fun toLocalTime(value: Long?): LocalTime? {
            return value?.let { LocalTime.ofNanoOfDay(it) }
        }




    @TypeConverter
    fun listToString(list: List<Int>?): String {
        return jsonAdapter.toJson(list)
    }

    @TypeConverter
    fun stringToList(jsonString: String): List<Int>? {
        return jsonAdapter.fromJson(jsonString)
    }


}