package com.sscott.studytime_4.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sscott.studytime_4.data.local.entities.*

@Database(entities = [StudySession::class, WeeklyGoal::class, MonthlyGoal::class, Duration::class], version = 35)
@TypeConverters(Converters::class)
abstract class StudyDatabase : RoomDatabase(){

    abstract fun studyDao() : StudyDao
}