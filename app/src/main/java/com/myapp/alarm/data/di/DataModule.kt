package com.myapp.alarm.data.di

import android.content.Context
import androidx.room.Room
import com.myapp.alarm.data.local.AlarmDao
import com.myapp.alarm.data.local.AlarmDatabase
import com.myapp.alarm.data.repo.AlarmRepoImpl
import com.myapp.alarm.domain.repo.AlarmRepo
import com.myapp.alarm.domain.usecase.GetAllAlarmUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn (SingletonComponent::class)
object DataModule {


    @Provides
   @Singleton
   fun provideDatabase (@ApplicationContext context: Context)
   : AlarmDatabase {
       return  Room.databaseBuilder(context,AlarmDatabase::class.java,
           "alarm_db",).fallbackToDestructiveMigration().build()
   }

    @Provides
    @Singleton
    fun provideAlarmDao ( alarmDatabase: AlarmDatabase):AlarmDao{
        return alarmDatabase.getAlarmDao()
    }
   @Provides
   fun provideRepo (alarmDao: AlarmDao):AlarmRepo{
       return AlarmRepoImpl(alarmDao)
   }

    @Provides
    fun provideGetAllAlarmsusecase (alarmRepo: AlarmRepo):GetAllAlarmUseCase{
        return GetAllAlarmUseCase(alarmRepo)
    }

}
