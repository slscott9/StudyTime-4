package com.sscott.studytime_4.ui.month.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.sscott.studytime_4.CoroutineTestRule
import com.sscott.studytime_4.data.MonthData
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.repo.Repository
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime

class MonthDetailViewModelTest {

    private lateinit var viewModel: MonthDetailViewModel

    private lateinit var repository: Repository

    private lateinit var studySessionList: List<StudySession>

    private lateinit var monthData : MonthData

    private lateinit var savedStateHandle: SavedStateHandle

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setup() {
        repository = mock()

        savedStateHandle = mock()


        viewModel = MonthDetailViewModel(repository, savedStateHandle)

//        studySessionList = listOf(
//            StudySession(
//                hours = 1F,
//                minutes = 60,
//                date = "2020-12-02", //formattedDate
//                weekDay = 3,
//                month = 12,
//                dayOfMonth = 2,
//                year = 2020,
//                startTime = "1: 30 am",
//                endTime = "2: 30 pm",
//                offsetDateTime = OffsetDateTime.now(),
//                epochDate = 1
//            ),
//
//            StudySession(
//                hours = 2F,
//                minutes = 120,
//                date = "2020-12-08", //formattedDate
//                weekDay = 2,
//                month = 12,
//                dayOfMonth = 8,
//                year = 2020,
//                startTime = "1: 30 am",
//                endTime = "2: 30 pm",
//                epochDate = 1,
//                offsetDateTime = OffsetDateTime.now()
//
//            ),
//
//            StudySession(
//                hours = 3F,
//                minutes = 180,
//                date = "2020-12-10", //formattedDate
//                weekDay = 4,
//                month = 12,
//                dayOfMonth = 10,
//                year = 2020,
//                startTime = "1: 30 am",
//                endTime = "2: 30 pm",
//                epochDate = 1,
//                offsetDateTime = OffsetDateTime.now()
//
//            )
//        )
//
//        monthData = MonthData(
//            labels = mock(),
//            monthBarData = mock(),
//            totalHours = 5F
//        )


    }


    @Test
    fun setMonthBarData() = runBlockingTest {



    }
}