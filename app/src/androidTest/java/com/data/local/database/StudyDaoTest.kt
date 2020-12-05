package com.data.local.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.data.getOrAwaitValue
import com.example.studytime_4.data.local.database.StudyDao
import com.example.studytime_4.data.local.database.StudyDatabase
import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.local.entities.WeeklyGoal
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.http.GET

@RunWith(JUnit4::class)
@SmallTest
class StudyDaoTest {

    private lateinit var database: StudyDatabase
    private lateinit var dao :StudyDao

    private lateinit var monthlyGoal1  : MonthlyGoal
    private lateinit var monthlyGoal2  : MonthlyGoal

    private lateinit var studySession1 : StudySession
    private lateinit var studySession2 : StudySession
    private lateinit var studySession3 : StudySession
    private lateinit var studySession4 : StudySession



    private lateinit var weeklyGoal1  : WeeklyGoal
    private lateinit var weeklyGoal2  : WeeklyGoal

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StudyDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.studyDao()

        studySession1 = StudySession(
            hours = 1F,
            minutes = 60,
            date = "2020-12-02", //formattedDate
            weekDay = "WEDNESDAY",
            month = 12,
            dayOfMonth = 2,
            year = 2020,
        )

        studySession2 = StudySession(
            hours = 2F,
            minutes = 120,
            date = "2020-12-08", //formattedDate
            weekDay = "SATURDAY",
            month = 12,
            dayOfMonth = 8,
            year = 2020,
        )

        studySession3 = StudySession(
            hours = 3F,
            minutes = 180,
            date = "2020-12-10", //formattedDate
            weekDay = "TUESDAY",
            month = 12,
            dayOfMonth = 10,
            year = 2020,
        )

        studySession4 = StudySession(
            hours = 3F,
            minutes = 180,
            date = "2020-12-11", //formattedDate
            weekDay = "TUESDAY",
            month = 12,
            dayOfMonth = 10,
            year = 2021,
        )




        monthlyGoal1 = MonthlyGoal(
            date = "2020-12-05",
            weekDay = "FRIDAY",
            dayOfMonth = 5,
            hours = 5,
            month = 12,
            year = 2020,
        )

        monthlyGoal2 = MonthlyGoal(
            date = "2020-12-08",
            weekDay = "TUESDAY",
            dayOfMonth = 8,
            hours = 10,
            month = 12,
            year = 2020,
        )

        weeklyGoal1 = WeeklyGoal(
            date = "2020-12-05",
            weekDay = "FRIDAY",
            dayOfMonth = 5,
            hours = 5,
            month = 12,
            year = 2020,
        )

        weeklyGoal2 = WeeklyGoal(
            date = "2020-12-15",
            weekDay = "TUESDAY",
            dayOfMonth = 15,
            hours = 15,
            month = 12,
            year = 2020,
        )


    }

    @After
    fun tearDown() {
        database.close()
    }

    //checkForWeeklyGoal returns WeeklyGoal for this week
    @Test
    fun getGoalForWeek() = runBlockingTest {


        //upsertWeeklyGoal is a Transaction and cannot be tested properly
        dao.insertWeeklyGoal(weeklyGoal1)
        dao.insertWeeklyGoal(weeklyGoal2)

        val goalToTest = dao.getGoalForWeek(12, 2020, 6).take(1).toList()

        assertThat(goalToTest[0]).isEqualTo(weeklyGoal1)
        assertThat(goalToTest.size).isEqualTo(1)



    }

    //should return a null when goal does not exist
    @Test
    fun checkForWeeklyGoal()  = runBlocking {


        val goal = dao.checkForWeeklyGoal(12, 2020, 2)

        assertThat(goal).isEqualTo(null)


    }

    //should update an existing goal
    @Test
    fun updateWeeklyGoal() = runBlocking<Unit> {

        dao.insertWeeklyGoal(weeklyGoal1)

        val updatedWeeklyGoal = weeklyGoal1.copy(hours = 100)
        dao.updateWeeklyGoal(updatedWeeklyGoal)

        val goalToTest = dao.getGoalForWeek(12, 2020, 5).take(1).toList()

        assertThat(goalToTest[0]).isEqualTo(updatedWeeklyGoal)
    }

    //should return last seven study sessions from todays date
   @Test
   fun getLastSevenSessions() = runBlocking {

       dao.insertStudySession(studySession1)
       dao.insertStudySession(studySession2)
        dao.insertStudySession(studySession3) //this studySesion is out of range of last seven days from now

       val lastSevenStudySessionsHours = dao.getLastSevenSessions(12, 8, 2020).asLiveData().getOrAwaitValue()

       assertThat(lastSevenStudySessionsHours.size).isEqualTo(2)



   }

    //should return DISTINCT years with study sessions
    @Test
    fun getYearsWithSessions() = runBlocking {

        dao.insertStudySession(studySession1)
        dao.insertStudySession(studySession2)
        dao.insertStudySession(studySession3)
        dao.insertStudySession(studySession4)

        val yearsWithStudySessions = dao.getYearsWithSessions().asLiveData().getOrAwaitValue()

        assertThat(yearsWithStudySessions.size).isEqualTo(2)
    }

    //should return list of months with study sessions for a selected year
    @Test
    fun getMonthWithSelectedYear() = runBlocking {
        dao.insertStudySession(studySession1)
        dao.insertStudySession(studySession4) //year is 2021 should not return this study session

        val monthList = dao.getMonthsWithSelectedYear(2020).asLiveData().getOrAwaitValue()

        assertThat(monthList.size).isEqualTo(1)
    }



}