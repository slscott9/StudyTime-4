package com.example.studytime_4.ui.month.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.studytime_4.CoroutineTestRule
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.repo.Repository
import com.example.studytime_4.getOrAwaitValue
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.http.GET

class MonthDetailViewModelTest {

    private lateinit var viewModel: MonthDetailViewModel

    private lateinit var repository: Repository

    private lateinit var studySessionList: List<StudySession>

    private lateinit var monthData : MonthData

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setup() {
        repository = mock()

        viewModel = MonthDetailViewModel(repository)

        studySessionList = listOf(
            StudySession(
                hours = 1F,
                minutes = 60,
                date = "2020-12-02", //formattedDate
                weekDay = "WEDNESDAY",
                month = 12,
                dayOfMonth = 2,
                year = 2020,
            ),

            StudySession(
                hours = 2F,
                minutes = 120,
                date = "2020-12-08", //formattedDate
                weekDay = "SATURDAY",
                month = 12,
                dayOfMonth = 8,
                year = 2020,
            ),

            StudySession(
                hours = 3F,
                minutes = 180,
                date = "2020-12-10", //formattedDate
                weekDay = "TUESDAY",
                month = 12,
                dayOfMonth = 10,
                year = 2020,
            )
        )

        monthData = MonthData(
            labels = mock(),
            monthBarData = mock()
        )


    }


    @Test
    fun setMonthBarData() = coroutineTestRule.testDispatcher.runBlockingTest {


        whenever(repository.getAllSessionsWithMatchingMonth(1)).thenReturn(listOf(studySessionList).asFlow())

        viewModel.setMonth(1)

        viewModel.monthBarData.getOrAwaitValue()



        verify(repository, times(1)).getAllSessionsWithMatchingMonth(any())


//        verify(viewModel, times(1)).setMonthBarData(studySessionList)

    }
}