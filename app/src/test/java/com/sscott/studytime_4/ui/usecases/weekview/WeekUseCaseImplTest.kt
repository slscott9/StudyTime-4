package com.sscott.studytime_4.ui.usecases.weekview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.sscott.studytime_4.MainCoroutineTestRule
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.other.util.TimeUtilImpl
import com.sscott.studytime_4.other.util.time.TimeUtil
import com.sscott.studytime_4.ui.usecases.weekview.WeekUseCase
import com.sscott.studytime_4.ui.usecases.weekview.WeekUseCaseImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime

class WeekUseCaseImplTest {

    private lateinit var weekUseCase: WeekUseCase
    private lateinit var repository: Repository
    private lateinit var timeUtil: TimeUtil
    private lateinit var studySession1 : StudySession
    private lateinit var studySession2 : StudySession
    private lateinit var studySessionList : List<StudySession>
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineTestRule = MainCoroutineTestRule()

    @Before
    fun setup() {
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
        timeUtil = TimeUtilImpl()
        repository = mock()

        weekUseCase = WeekUseCaseImpl(repository, timeUtil)
    }

    @Test
    fun `totalHours() returns sum of studySessionList`() {


        val totalHoursSubject = weekUseCase.totalHours(studySessionList)

        //expected, actual
        assertEquals(180F, totalHoursSubject)
    }

    @Test
    fun `toBarDataSet converts study session list to BarDataSet`() {

        val subject = weekUseCase.toBarDataSet(studySessionList)

        assertNotNull(subject)

    }

    @Test
    fun `toBarDataSet positions studySessions according to weekday`() {

        val subject = weekUseCase.toBarDataSet(studySessionList)

        //weekdays are 0 sunday 1 monday, 2 tuesday, 3 wednesday 4 thursday, 5 friday, 6 saturday
        //in order to get study session hours to be displayed for the correct weekday an array must be prepopulated with size 7 (for each weekday) for mpandroid
        //then looping through the array use the weekday property to set it accordingly in the array
        //hours are displayed for y coordinates indices are x coordinates
        assertEquals(1F, subject.getEntryForIndex(3).y)
        assertEquals(2F, subject.getEntryForIndex(4).y)
    }






}