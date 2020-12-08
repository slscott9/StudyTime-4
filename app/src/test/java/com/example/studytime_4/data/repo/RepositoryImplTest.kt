package com.example.studytime_4.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.studytime_4.CoroutineTestRule
import com.example.studytime_4.data.local.database.StudyDao
import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.WeeklyGoal
import com.example.studytime_4.dispatcher.DispatcherProvider
import com.example.studytime_4.dispatcher.DispatcherProviderImpl
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RepositoryImplTest {

    private lateinit var repository: Repository
    private lateinit var dao: StudyDao

    private lateinit var weeklyGoal: WeeklyGoal
    private lateinit var monthlyGoal: MonthlyGoal

//   @get:Rule
//   var coroutineTestRule = CoroutineTestRule()

    @Before
    fun setup() {

        monthlyGoal = MonthlyGoal(
            date = "2020-12-05",
            weekDay = "FRIDAY",
            dayOfMonth = 5,
            hours = 5,
            month = 12,
            year = 2020,
        )
        weeklyGoal = WeeklyGoal(
            date = "2020-12-15",
            weekDay = "TUESDAY",
            dayOfMonth = 15,
            hours = 15,
            month = 12,
            year = 2020,
        )

        dao = mock()

        repository = RepositoryImpl(dao)

    }

    //saveWeeklyGoal should call dao.checkForWeeklyGoal and dao.upsertWeeklyGoal once when a WeeklyGoal exists
    @ExperimentalCoroutinesApi
    @Test
    fun saveWeeklyGoal() = runBlockingTest {


        whenever(dao.checkForWeeklyGoal(any(), any(), any())).thenReturn(weeklyGoal)

        repository.saveWeeklyGoal(weeklyGoal)

        verify(dao, times(1)).checkForWeeklyGoal(any(), any(), any())
        verify(dao, times(1)).upsertWeeklyGoal(any())
    }

    //saveWeeklyGoal should call checkForWeeklyGoal and upsertWeeklyGoal once when goal is null
    @ExperimentalCoroutinesApi
    @Test
    fun saveWeeklyGoal2() = runBlockingTest {
        whenever(dao.checkForWeeklyGoal(any(), any(), any())).thenReturn(null)

        repository.saveWeeklyGoal(weeklyGoal)

        verify(dao, times(1)).checkForWeeklyGoal(any(), any(), any())
        verify(dao, times(1)).upsertWeeklyGoal(any())
    }

    //saveMonthlyGoal calls dao.checkForMonthlyGoal and dao.insertMonthlyGoal once when goal is returned
    @ExperimentalCoroutinesApi
    @Test
    fun saveMonthlyGoal() = runBlockingTest {

        whenever(dao.checkForMonthlyGoal(any(), any(), any())).thenReturn(monthlyGoal)

        repository.saveMonthlyGoal(monthlyGoal)

        verify(dao, times(1)).checkForMonthlyGoal(any(), any(), any())
        verify(dao, times(1)).upsertMonthlyGoal(any())
    }

    //saveMonthlyGoal calls dao.checkForMonthlyGoal and dao.upsertMonthlyGoal once whenever null is returned from dao.checkForWeeklyGoal
    @ExperimentalCoroutinesApi
    @Test
    fun saveMonthlyGoal2() = runBlockingTest{

        whenever(dao.checkForMonthlyGoal(any(), any(), any())).thenReturn(null)

        repository.saveMonthlyGoal(monthlyGoal)

        verify(dao, times(1)).checkForMonthlyGoal(any(), any(), any())
        verify(dao, times(1)).upsertMonthlyGoal(any())
    }
}