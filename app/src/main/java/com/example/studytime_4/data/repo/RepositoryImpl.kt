package com.example.studytime_4.data.repo

import com.example.studytime_4.data.local.database.StudyDao
import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.local.entities.WeeklyGoal
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class RepositoryImpl @Inject constructor (private val dao: StudyDao) : Repository {


    //WEEKLY GOAL

    override fun getGoalForWeek(curMonth: Int, curYear: Int, currentDayOfMonth: Int): Flow<WeeklyGoal?> {
        return dao.getGoalForWeek(curMonth, curYear, currentDayOfMonth)
    }

    override suspend fun saveWeeklyGoal(weeklyGoal: WeeklyGoal) {

        val goal = dao.checkForWeeklyGoal(weeklyGoal.month, weeklyGoal.year, weeklyGoal.dayOfMonth)

        if(goal == null){
            dao.upsertWeeklyGoal(weeklyGoal)
        }else{
           val updateGoal = goal.copy(hours = weeklyGoal.hours)
            dao.upsertWeeklyGoal(updateGoal)
        }
    }



    //MONTHLY GOAL

    override suspend fun saveMonthlyGoal(monthlyGoal: MonthlyGoal) {

        val goal = dao.checkForMonthlyGoal(monthlyGoal.month, monthlyGoal.year, monthlyGoal.dayOfMonth)

        if(goal == null){
            dao.upsertMonthlyGoal(monthlyGoal)
        }else{
            val updateGoal = goal.copy(hours = monthlyGoal.hours)
            dao.upsertMonthlyGoal(updateGoal)
        }
    }


    //SESSIONS

    override fun getLastSevenSessionsHours(
        currentMonth: Int,
        currentDayOfMonth: Int
    ): Flow<List<Float>> {
        return dao.getLastSevenSessionsHours(currentMonth, currentDayOfMonth)
    }

    override fun getSessionHoursForMonth(monthSelected: Int): Flow<List<Float>> {
        return dao.getSessionHoursForMonth(monthSelected)

    }

    override suspend fun getCurrentStudySession(currentDate: String): StudySession {
        return dao.getCurrentStudySession(currentDate)

    }

    override fun getAllSessionsWithMatchingMonth(monthSelected: Int): Flow<List<StudySession>> {
        return dao.getAllSessionsWithMatchingMonth(monthSelected)

    }

    override  fun getLastSevenSessions(
        currentMonth: Int,
        currentDayOfMonth: Int,
        curYear: Int
    ): Flow<List<StudySession>> {
        return dao.getLastSevenSessions(currentMonth, currentDayOfMonth, curYear)

    }

    override fun getYearsWithSessions(): Flow<List<Int>> {
        return dao.getYearsWithSessions()

    }

    override fun getMonthsWithSelectedYear(yearSelected: Int): Flow<List<Int>> {
        return dao.getMonthsWithSelectedYear(yearSelected)

    }

    override suspend fun upsertStudySession(studySession: StudySession) : Long{
        return dao.upsertStudySession(studySession)

    }

}