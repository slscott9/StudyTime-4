package com.example.studytime_4.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.studytime_4.data.local.entities.Converters
import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.local.entities.WeeklyGoal

@Database(entities = [StudySession::class, WeeklyGoal::class, MonthlyGoal::class], version = 22)
@TypeConverters(Converters::class)
abstract class StudyDatabase : RoomDatabase(){

    abstract fun studyDao() : StudyDao
}