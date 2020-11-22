package com.example.studytime_4.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studytime_4.data.local.entities.StudySession

@Database(entities = [StudySession::class], version = 1)
abstract class StudyDatabase : RoomDatabase(){

    abstract fun studyDao() : StudyDao
}