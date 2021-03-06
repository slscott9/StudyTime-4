package com.data.local.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.sscott.studytime_4.data.local.database.StudyDao
import com.sscott.studytime_4.data.local.database.StudyDatabase
import com.sscott.studytime_4.data.local.entities.MonthlyGoal
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

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

        /*
            weekDay should be 1 to monday, 2 to tuesday, 3 to wednesday, 4 to thursday , 5 to friday , 6 to saturday, 7 to sunday
         */
//
//        studySession1 = StudySession(
//            minutes = 60F,
//            date = "2020-12-20", //formattedDate
//            weekDay = 7,
//            month = 12,
//            dayOfMonth = 20,
//            year = 2020,
//            startTime = "1",
//            endTime = "3",
//            epochDate = 1608524461,
//            offsetDateTime =
//        )
//
//        studySession2 = StudySession(
//            minutes = 120F,
//            date = "2020-12-26", //formattedDate
//            weekDay = 6,
//            month = 12,
//            dayOfMonth = 26,
//            year = 2020,
//            startTime = "1",
//            endTime = "3",
//            epochDate = 1609042861
//        )





//        monthlyGoal1 = MonthlyGoal(
//            date = "2020-12-05",
//            weekDay = "FRIDAY",
//            dayOfMonth = 5,
//            hours = 5,
//            month = 12,
//            year = 2020,
//        )
//
//        monthlyGoal2 = MonthlyGoal(
//            date = "2020-12-08",
//            weekDay = "TUESDAY",
//            dayOfMonth = 8,
//            hours = 10,
//            month = 12,
//            year = 2020,
//        )
//
//        weeklyGoal1 = WeeklyGoal(
//            date = "2020-12-05",
//            weekDay = "FRIDAY",
//            dayOfMonth = 5,
//            hours = 5,
//            month = 12,
//            year = 2020,
//        )
//
//        weeklyGoal2 = WeeklyGoal(
//            date = "2020-12-15",
//            weekDay = "TUESDAY",
//            dayOfMonth = 15,
//            hours = 15,
//            month = 12,
//            year = 2020,
//        )


    }

    @After
    fun tearDown() {
        database.close()
    }


//    //getLastSevenSessions should return any study sessions between todays date and todays date minus todays day of the week
//    @Test
//    fun getLastSevenSessions2() = runBlockingTest {
//
//        dao.insertStudySession(studySession1)
//        dao.insertStudySession(studySession2)
//
//        val list = dao.getLastSevenSessions(6 * 86400).asLiveData().getOrAwaitValue()
//
//        assertThat(list.size).isEqualTo(2)
//    }
//
//    //checkForWeeklyGoal returns WeeklyGoal for this week
//    @Test
//    fun getGoalForWeek() = runBlockingTest {
//
//
//        //upsertWeeklyGoal is a Transaction and cannot be tested properly
//        dao.insertWeeklyGoal(weeklyGoal1)
//        dao.insertWeeklyGoal(weeklyGoal2)
//
//        val goalToTest = dao.getGoalForWeek(12, 2020, 6).take(1).toList()
//
//        assertThat(goalToTest[0]).isEqualTo(weeklyGoal1)
//        assertThat(goalToTest.size).isEqualTo(1)
//
//
//
//    }
//
//    //should return a null when goal does not exist
//    @Test
//    fun checkForWeeklyGoal()  = runBlocking {
//
//
//        val goal = dao.checkForWeeklyGoal(12, 2020, 2)
//
//        assertThat(goal).isEqualTo(null)
//
//
//    }
//
//    //should update an existing goal
//    @Test
//    fun updateWeeklyGoal() = runBlocking<Unit> {
//
//        dao.insertWeeklyGoal(weeklyGoal1)
//
//        val updatedWeeklyGoal = weeklyGoal1.copy(hours = 100)
//        dao.updateWeeklyGoal(updatedWeeklyGoal)
//
//        val goalToTest = dao.getGoalForWeek(12, 2020, 5).take(1).toList()
//
//        assertThat(goalToTest[0]).isEqualTo(updatedWeeklyGoal)
//    }
//
//    //should return last seven study sessions from todays date
////   @Test
////   fun getLastSevenSessions() = runBlocking {
////
////       dao.insertStudySession(studySession1)
////       dao.insertStudySession(studySession2)
////        dao.insertStudySession(studySession3) //this studySesion is out of range of last seven days from now
////
////       val lastSevenStudySessionsHours = dao.getLastSevenSessions(12, 8, 2020).asLiveData().getOrAwaitValue()
////
////       assertThat(lastSevenStudySessionsHours.size).isEqualTo(2)
////
////
////
////   }
//
//    //should return DISTINCT years with study sessions
//    @Test
//    fun getYearsWithSessions() = runBlocking {
//
//        dao.insertStudySession(studySession1)
//        dao.insertStudySession(studySession2)
//        dao.insertStudySession(studySession3)
//        dao.insertStudySession(studySession4)
//
//        val yearsWithStudySessions = dao.getYearsWithSessions().asLiveData().getOrAwaitValue()
//
//        assertThat(yearsWithStudySessions.size).isEqualTo(2)
//    }
//
//    //should return list of months with study sessions for a selected year
//    @Test
//    fun getMonthWithSelectedYear() = runBlocking {
//        dao.insertStudySession(studySession1)
//        dao.insertStudySession(studySession4) //year is 2021 should not return this study session
//
//        val monthList = dao.getMonthsWithSelectedYear(2020).asLiveData().getOrAwaitValue()
//
//        assertThat(monthList.size).isEqualTo(1)
//    }



}