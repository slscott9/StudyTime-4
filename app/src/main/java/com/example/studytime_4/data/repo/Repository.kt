package com.example.studytime_4.data.repo

import com.example.studytime_4.data.local.entities.StudySession
import kotlinx.coroutines.flow.Flow

interface Repository {

    //Changes for transformations
    fun getLastSevenSessionsHours(currentMonth: Int, currentDayOfMonth: Int): Flow<List<Float>>


    fun getSessionHoursForMonth(monthSelected: Int): Flow<List<Float>>


    /*
        Should we return live data straight form the dao? The repo methods can receive parameters that we can use to query the database
     */

    suspend fun getCurrentStudySession(currentDate: String): StudySession


    fun getAllSessionsWithMatchingMonth(monthSelected: Int): Flow<List<StudySession>>


    /*
        To get the current week's study sessions query database for current day of week
     */

    fun getLastSevenSessions(currentMonth: Int, currentDayOfMonth: Int): Flow<List<StudySession>>


    //Change to return study sessions
    fun getYearsWithSessions(): Flow<List<Int>>

    fun getMonthsWithSelectedYear(yearSelected : Int) : Flow<List<Int>>

    suspend fun upsertStudySession(studySession: StudySession) : Long
}