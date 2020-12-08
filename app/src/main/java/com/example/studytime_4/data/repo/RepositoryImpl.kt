package com.example.studytime_4.data.repo

import com.example.studytime_4.data.local.database.StudyDao
import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.local.entities.WeeklyGoal
import com.example.studytime_4.di.DispatcherModule
import com.example.studytime_4.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RepositoryImpl @Inject constructor (
    private val dao: StudyDao,
) : Repository {

    /*
        Room suspending functions and functions that return live/flow are main safe and dispatchers are taken care of for us
        No need to use withContext(dispatcher.io)
     */

    //WEEKLY GOAL

    override fun getGoalForWeek(curMonth: Int, curYear: Int, currentDayOfMonth: Int): Flow<WeeklyGoal?> {
        return dao.getGoalForWeek(curMonth, curYear, currentDayOfMonth)
    }

    override suspend fun saveWeeklyGoal(weeklyGoal: WeeklyGoal){


        Timber.i(Thread.currentThread().name)
        val goal = dao.checkForWeeklyGoal(weeklyGoal.month, weeklyGoal.year, weeklyGoal.dayOfMonth)

        if(goal == null){
            dao.upsertWeeklyGoal(weeklyGoal)
            Timber.i(Thread.currentThread().name)

        }else{
           val updateGoal = goal.copy(hours = weeklyGoal.hours)
            dao.upsertWeeklyGoal(updateGoal)
            Timber.i(Thread.currentThread().name)

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

    override suspend fun getCurrentStudySession(currentDate: String)  =
        dao.getCurrentStudySession(currentDate)



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


    //Inject ioDispatcher for this operation
    override suspend fun upsertStudySession(studySession: StudySession) =
        dao.upsertStudySession(studySession)


}