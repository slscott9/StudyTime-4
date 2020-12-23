package com.example.studytime_4.data.repo

import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.local.entities.WeeklyGoal
import kotlinx.coroutines.flow.Flow

interface Repository {


    //GET AND SAVE MONTHLY GOAL

    fun monthlyGoal(yearSelected: Int, monthSelected: Int) : Flow<MonthlyGoal?>

    suspend fun saveMonthlyGoal(monthlyGoal: MonthlyGoal) : Long



    //GET AND SAVE WEEKLY GOAL
    fun weeklyGoal(curMonth: Int, curYear: Int, currentDayOfMonth: Int) : Flow<WeeklyGoal?>

    suspend fun saveWeeklyGoal(weeklyGoal: WeeklyGoal) : Long


    //GET WEEKLY SESSIONS AND HOURS
    fun weeklyHours(currentMonth: Int, currentDayOfMonth: Int): Flow<List<Float>>

    fun weeklyStudySessions(): Flow<List<StudySession>>



    //GET MONTHLY HOURS AND STUDY SESSIONS

    fun monthlyHours(monthSelected: Int): Flow<List<Float>>

    fun monthlyStudySessions(monthSelected: Int, yearSelected: Int): Flow<List<StudySession>>


    //GET YEARS AND MONTH WITH STUDY SESSIONS

    fun allYearsWithSessions(): Flow<List<Int>>

    fun monthsWithSessions(yearSelected : Int) : Flow<List<Int>>


    //SAVE STUDY SESSION

    suspend fun upsertStudySession(studySession: StudySession) : Long
}