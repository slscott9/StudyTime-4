package com.sscott.studytime_4.data.repo

import androidx.lifecycle.LiveData
import com.sscott.studytime_4.data.local.entities.*
import kotlinx.coroutines.flow.Flow

interface Repository {


    //GET AND SAVE MONTHLY GOAL

    fun monthlyGoal(curYear: Int, curMonth: Int) : Flow<MonthlyGoal?>

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


//    suspend fun insertStudySession(studySession: StudySession)

    suspend fun insertStudyDuration(duration: Duration)


    fun studyDurations(date : String) : LiveData<Durations>

}