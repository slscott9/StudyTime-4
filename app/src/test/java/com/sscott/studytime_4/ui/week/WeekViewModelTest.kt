package com.sscott.studytime_4.ui.week

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.sscott.studytime_4.CoroutineTestRule
import com.sscott.studytime_4.MainCoroutineTestRule
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.getOrAwaitValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.mock
import java.time.OffsetDateTime
import kotlin.coroutines.coroutineContext

class WeekViewModelTest {

    private lateinit var viewModel: WeekViewModel
    private lateinit var repository: Repository

    private lateinit var studySession1 : StudySession
    private lateinit var studySession2 : StudySession

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineTestRule = MainCoroutineTestRule()


//    @Before
//    fun setup() {
//
//        studySession1 = StudySession(
//                minutes = 60F,
//                date = "2020-12-02", //formattedDate
//                weekDay = 3,
//                month = 12,
//                dayOfMonth = 2,
//                year = 2020,
//                startTime = "1: 30 am",
//                endTime = "2: 30 pm",
//                offsetDateTime = OffsetDateTime.now(),
//                epochDate = 1
//        )
//        studySession2 = StudySession(
//            minutes = 120F,
//            date = "2020-12-03", //formattedDate
//            weekDay = 2,
//            month = 12,
//            dayOfMonth = 3,
//            year = 2020,
//            startTime = "1: 30 am",
//            endTime = "2: 30 pm",
//            offsetDateTime = OffsetDateTime.now(),
//            epochDate = 1
//        )
//
//        repository = mock()
//        viewModel = WeekViewModel(repository)
//    }
//
//    @Test
//    fun `when repo returns weekly study sessions weekBarData changes`() {
//
//        val weeklyStudySessions = listOf(studySession1, studySession2,)
//
//        whenever(repository.weeklyStudySessions()).thenReturn(
//            flow { weeklyStudySessions }
//
//        )
//
//        val observer : Observer<List<StudySession>> = mock()
//
//        val data = viewModel.sessionsForWeek.asLiveData()
//        data.observeForever(observer)
//
//        assertNotNull(viewModel.sessionsForWeek)
//    }
//
//    @Test
//    fun `when repo returns weeklyHours goalData changes` () = runBlockingTest {
//
//
//    }


}