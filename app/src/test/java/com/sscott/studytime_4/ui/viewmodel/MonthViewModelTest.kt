package com.sscott.studytime_4.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.nhaarman.mockitokotlin2.*
import com.sscott.studytime_4.CoroutineTestRule
import com.sscott.studytime_4.MainCoroutineTestRule
import com.sscott.studytime_4.data.local.database.StudyDao
import com.sscott.studytime_4.data.local.entities.MonthlyGoal
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.data.repo.RepositoryImpl
import com.sscott.studytime_4.getOrAwaitValue
import com.sscott.studytime_4.other.util.time.TimeUtil
import com.sscott.studytime_4.ui.month.MonthViewModel
import com.sscott.studytime_4.ui.usecases.monthview.MonthUseCase
import com.sscott.studytime_4.ui.usecases.monthview.MonthUseCaseImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.time.OffsetDateTime

class MonthViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineTestRule = CoroutineTestRule()

    //test subject
    lateinit var viewModel: MonthViewModel

    //collaborators
    lateinit var monthUseCase: MonthUseCase
    //utilities
    private lateinit var studySession1 : StudySession
    private lateinit var studySession2 : StudySession
    private lateinit var studySessionList : List<StudySession>
    private lateinit var labelList : List<String>

    @Mock
    private lateinit var observer : Observer<List<String>>
    private lateinit var monthlyGoal: MonthlyGoal


    @Before
    fun setup() {


        MockitoAnnotations.openMocks(this)

        monthlyGoal = MonthlyGoal(
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

        studySessionList = listOf(studySession1, studySession2)

        labelList = listOf("hey")

        monthUseCase = mock()

        //monthLabels, totalHours, monthBarDataSet all mapped from sessionsForMonth in view model
        whenever(monthUseCase.sessionsForMonth()).thenReturn(flowOf(studySessionList))

        //set immediately when view model is instantied must be set before others
        whenever(monthUseCase.monthlyGoal()).thenReturn(flowOf(monthlyGoal))
    }

    @Test
    fun `sessionForMonth changes when repo returns flow`() = mainCoroutineTestRule.testDispatcher.runBlockingTest {

        whenever(monthUseCase.sessionsForMonth()).thenReturn(flowOf(studySessionList))
        whenever(monthUseCase.monthlyGoal()).thenReturn(flowOf(monthlyGoal))

        viewModel = MonthViewModel(monthUseCase, mainCoroutineTestRule.testDispatcher)

        viewModel.sessionsForMonth.collectIndexed {index, value ->
            if (index == 0) assertEquals(studySession1, value[0])
        }
    }

    @Test
    fun `sessionsForMonth changes monthLabels changes` () = mainCoroutineTestRule.testDispatcher.runBlockingTest{

        whenever(monthUseCase.setLabels(any())).thenReturn(labelList)
        viewModel = MonthViewModel(monthUseCase, mainCoroutineTestRule.testDispatcher)

        val subject = viewModel.monthLabels.getOrAwaitValue() //registers observer

        assertEquals(labelList, subject)
    }

    @Test
    fun `totalHours changes when sessionsForMonth changes`() = mainCoroutineTestRule.testDispatcher.runBlockingTest {

        val expected = BarDataSet(listOf(BarEntry(1F, 1F)), "label")
        whenever(monthUseCase.totalHours(any())).thenReturn(expected)

        viewModel = MonthViewModel(monthUseCase, mainCoroutineTestRule.testDispatcher)

        val subject = viewModel.totalHours.getOrAwaitValue()

        assertEquals(expected, subject)

    }

    @Test
    fun `monthlyGoal emits successfully`() = mainCoroutineTestRule.testDispatcher.runBlockingTest {

        whenever(monthUseCase.monthlyGoal()).thenReturn(flowOf(monthlyGoal))

        viewModel = MonthViewModel(monthUseCase, mainCoroutineTestRule.testDispatcher)

        val subject = viewModel.monthlyGoal.getOrAwaitValue()

        assertEquals(monthlyGoal, subject)
    }

    @Test
    fun `viewmodel saveGoal call monthUseCase saveGoal`() = mainCoroutineTestRule.testDispatcher.runBlockingTest {

        viewModel = MonthViewModel(monthUseCase, mainCoroutineTestRule.testDispatcher)

        viewModel.saveGoal(1)

        verify(monthUseCase, times(1)).saveGoal(any())

    }
}