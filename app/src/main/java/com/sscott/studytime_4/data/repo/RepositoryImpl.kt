package com.sscott.studytime_4.data.repo

import androidx.lifecycle.LiveData
import com.sscott.studytime_4.data.local.database.StudyDao
import com.sscott.studytime_4.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor (
    private val dao: StudyDao,
) : Repository {

    /*
        Room suspending functions and functions that return live/flow are main safe and dispatchers are taken care of for us
        No need to use withContext(dispatcher.io)
     */

    //MONTHLY GOAL

    override fun monthlyGoal(yearSelected: Int, monthSelected: Int): Flow<MonthlyGoal?> {
        return dao.monthlyGoal(yearSelected, monthSelected)
    }

    override suspend fun saveMonthlyGoal(monthlyGoal: MonthlyGoal) : Long {

        val goal = dao.checkForMonthlyGoal(monthlyGoal.month, monthlyGoal.year, monthlyGoal.dayOfMonth)

        return if(goal == null){
            dao.upsertMonthlyGoal(monthlyGoal)
        }else{
            val updateGoal = goal.copy(hours = monthlyGoal.hours)
            dao.upsertMonthlyGoal(updateGoal)
        }
    }


    //WEEKLY GOAL

    override fun weeklyGoal(curMonth: Int, curYear: Int, currentDayOfMonth: Int): Flow<WeeklyGoal?> {
        return dao.weeklyGoal(curMonth, curYear, currentDayOfMonth)
    }

    override suspend fun saveWeeklyGoal(weeklyGoal: WeeklyGoal) : Long {
        val goal = dao.checkForWeeklyGoal(weeklyGoal.month, weeklyGoal.year, weeklyGoal.dayOfMonth)

        return if(goal == null){
            dao.upsertWeeklyGoal(weeklyGoal)

        }else{
           val updateGoal = goal.copy(hours = weeklyGoal.hours)
            dao.upsertWeeklyGoal(updateGoal)

        }
    }

    //WEEKLY SESSIONS AND HOURS

    override fun weeklyHours(
        currentMonth: Int,
        currentDayOfMonth: Int
    ): Flow<List<Float>> {
        return dao.weeklyHours(currentMonth, currentDayOfMonth)
    }

    override  fun weeklyStudySessions(
    ): Flow<List<StudySession>> {
        return dao.weeklyStudySessions()

    }



    //GET MONTHLY HOURS AND STUDY SESSIONS

    override fun monthlyHours(monthSelected: Int): Flow<List<Float>> {
        return dao.monthlyHours(monthSelected)

    }

    override fun monthlyStudySessions(monthSelected: Int, yearSelected: Int): Flow<List<StudySession>> {
        return dao.monthlyStudySessions(monthSelected, yearSelected)

    }


    //GET YEARS AND MONTH WITH STUDY SESSIONS
    override fun allYearsWithSessions(): Flow<List<Int>> {
        return dao.allYearsWithSessions()

    }

    override fun monthsWithSessions(yearSelected: Int): Flow<List<Int>> {
        return dao.monthsWithSessions(yearSelected)

    }


    //SAVE STUDY SESSION

    override suspend fun upsertStudySession(studySession: StudySession) =
        dao.upsertStudySession(studySession)

//    override suspend fun insertStudySession(studySession: StudySession) {
//        dao.insertStudySession(studySession)
//    }


    override suspend fun insertStudyDuration(duration: Duration) {
        dao.insertStudyDuration(duration)
    }

    override fun studyDurations(date: String): LiveData<Durations> {
        return dao.studyDurations(date)
    }
}