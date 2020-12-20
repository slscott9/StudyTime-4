package com.example.studytime_4.data.repo

import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.local.entities.WeeklyGoal
import kotlinx.coroutines.flow.Flow

interface Repository {

    //WEEKLY GOAL
    fun getGoalForWeek(curMonth: Int, curYear: Int, currentDayOfMonth: Int) : Flow<WeeklyGoal?>

    suspend fun saveWeeklyGoal(weeklyGoal: WeeklyGoal) : Long

    //MONTHLY GOAL
    suspend fun saveMonthlyGoal(monthlyGoal: MonthlyGoal) : Long

    fun getMonthlyGoal(yearSelected: Int, monthSelected: Int) : Flow<MonthlyGoal?>


    //Changes for transformations
    fun getLastSevenSessionsHours(currentMonth: Int, currentDayOfMonth: Int): Flow<List<Float>>


    fun getSessionHoursForMonth(monthSelected: Int): Flow<List<Float>>


    /*
        Should we return live data straight form the dao? The repo methods can receive parameters that we can use to query the database
     */

    suspend fun getCurrentStudySession(currentDate: String): StudySession


    fun getAllSessionsWithMatchingMonth(monthSelected: Int, yearSelected: Int): Flow<List<StudySession>>


    /*
        To get the current week's study sessions query database for current day of week
     */

//    fun getLastSevenSessions(currentWeekDay: Int, currentMonth: Int, currentDayOfMonth: Int, curYear: Int): Flow<List<StudySession>>
//fun getLastSevenSessions(weekDayEpoch : Long): Flow<List<StudySession>>
fun getLastSevenSessions(weekDay : Int): Flow<List<StudySession>>



    fun getYearsWithSessions(): Flow<List<Int>>

    fun getMonthsWithSelectedYear(yearSelected : Int) : Flow<List<Int>>

    suspend fun upsertStudySession(studySession: StudySession) : Long
}