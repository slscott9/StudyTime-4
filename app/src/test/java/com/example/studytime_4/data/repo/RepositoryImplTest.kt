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

        //weekday is sunday = 0 saturday = 6

        monthlyGoal = MonthlyGoal(
            date = "2020-12-20",
            weekDay = 0,
            dayOfMonth = 20,
            hours = 5,
            month = 12,
            year = 2020,
        )
        weeklyGoal = WeeklyGoal(
            date = "2020-12-21",
            weekDay = 1,
            dayOfMonth = 20,
            hours = 15,
            month = 12,
            year = 2020,
        )

        dao = mock()

        repository = RepositoryImpl(dao)

    }

    //saveMonthlyGoal should call dao.upsertMonthlyGoal when goal is null
    @Test
    fun saveMonthlyGoal() = runBlockingTest {

        whenever(dao.checkForMonthlyGoal(any(), any(), any())).thenReturn(null)
        whenever(dao.upsertMonthlyGoal(any())).thenReturn(1L)

        repository.saveMonthlyGoal(monthlyGoal)

        verify(dao, times(1)).upsertMonthlyGoal(any())
    }

    //saveWeeklyGoal should call dao.checkForWeeklyGoal and dao.upsertWeeklyGoal once when a weekly goal does exist
    @ExperimentalCoroutinesApi
    @Test
    fun saveWeeklyGoal() = runBlockingTest {

        whenever(dao.checkForWeeklyGoal(any(), any(), any())).thenReturn(weeklyGoal)
        whenever(dao.upsertWeeklyGoal(any())).thenReturn(1L)


        repository.saveWeeklyGoal(weeklyGoal)

        verify(dao, times(1)).checkForWeeklyGoal(any(), any(), any())
        verify(dao, times(1)).upsertWeeklyGoal(any())
    }

    //saveWeeklyGoal should call checkForWeeklyGoal and upsertWeeklyGoal once when goal is null
    @ExperimentalCoroutinesApi
    @Test
    fun saveWeeklyGoal2() = runBlockingTest {
        whenever(dao.checkForWeeklyGoal(any(), any(), any())).thenReturn(null)
        whenever(dao.upsertWeeklyGoal(any())).thenReturn(-1L)


        repository.saveWeeklyGoal(weeklyGoal)

        verify(dao, times(1)).checkForWeeklyGoal(any(), any(), any())
        verify(dao, times(1)).upsertWeeklyGoal(any())
    }


}