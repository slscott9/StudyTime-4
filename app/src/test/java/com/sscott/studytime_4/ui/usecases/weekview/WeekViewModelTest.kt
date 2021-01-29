package com.sscott.studytime_4.ui.usecases.weekview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.sscott.studytime_4.CoroutineTestRule
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.getOrAwaitValue
import com.sscott.studytime_4.other.util.time.TimeUtil
import com.sscott.studytime_4.ui.week.WeekViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import java.time.OffsetDateTime

class WeekViewModelTest {

    private lateinit var viewModel: WeekViewModel
    private lateinit var repository: Repository
    private lateinit var timeUtil: TimeUtil
    private lateinit var weekUseCase: WeekUseCase

    private lateinit var studySession1 : StudySession
    private lateinit var studySession2 : StudySession
    private lateinit var studySessionList : List<StudySession>
    private lateinit var weeklyGoal: WeeklyGoal

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineTestRule = CoroutineTestRule()

    @Before
    fun setup(){

        weeklyGoal = WeeklyGoal(
            date = "2020-12-04",
            weekDay = 3,
            dayOfMonth = 4,
            hours = 6,
            month = 12,
            year = 2020

        )
        studySession1 = StudySession(
            minutes = 60F,
            date = "2020-12-02", //formattedDate
            weekDay = 3,
            month = 12,
            dayOfMonth = 2,
            year = 2020,
            startTime = "1: 30 am",
            endTime = "2: 30 pm",
            offsetDateTime = OffsetDateTime.now(),
            epochDate = 1
        )
        studySession2 = StudySession(
            minutes = 120F,
            date = "2020-12-03", //formattedDate
            weekDay = 4,
            month = 12,
            dayOfMonth = 3,
            year = 2020,
            startTime = "1: 30 am",
            endTime = "2: 30 pm",
            offsetDateTime = OffsetDateTime.now(),
            epochDate = 1
        )

        studySessionList = listOf(
            studySession1, studySession2
        )

        repository = mock()
        timeUtil = mock()
        weekUseCase = mock()

    }

    @Test
    fun `viewmodel properties set to useCase oneshot functions`() = mainCoroutineTestRule.testDispatcher.runBlockingTest{
        whenever(weekUseCase.sessionsForWeek()).thenReturn(flowOf(studySessionList))
        whenever(weekUseCase.weeklyGoal()).thenReturn(flowOf(weeklyGoal))

        viewModel = WeekViewModel(weekUseCase, mainCoroutineTestRule.testDispatcher)


        viewModel.sessionsForWeek.collectIndexed { index, value ->
            if(index == 0) assertEquals(studySession1, value[0])
            if(index == 1) assertEquals(studySession2, value[1])
        }

        val subject = viewModel.weeklyGoal.getOrAwaitValue()

        assertEquals(weeklyGoal, subject)


    }

    /*
        CREATE NEW BRANCH!!!!!!1
        !!!!!!!!!!!!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!!!!!!!!!!
        !!!!!!!!!!!!!!!!!!!!
     */

    @Test
    fun `viewModel weeklyGoal set to weekUseCase weeklyGoal() return value`() = mainCoroutineTestRule.testDispatcher.runBlockingTest {
        whenever(weekUseCase.sessionsForWeek()).thenReturn(flowOf(studySessionList))
        whenever(weekUseCase.weeklyGoal()).thenReturn(flowOf(weeklyGoal))
        whenever(weekUseCase.totalHours(mock())).thenReturn(1F)

        viewModel = WeekViewModel(weekUseCase, mainCoroutineTestRule.testDispatcher)

        viewModel.sessionsForWeek.collect {  }


        val subject = viewModel.totalHours.getOrAwaitValue()

        assertEquals(1F, subject)

    }




}