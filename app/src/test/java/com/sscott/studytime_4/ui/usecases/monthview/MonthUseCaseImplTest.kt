package com.sscott.studytime_4.ui.usecases.monthview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.sscott.studytime_4.MainCoroutineTestRule
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.other.util.TimeUtilImpl
import com.sscott.studytime_4.other.util.time.TimeUtil
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.OffsetDateTime

class MonthUseCaseImplTest {

    private lateinit var repository: Repository
    private lateinit var timeUtil: TimeUtil
    private lateinit var monthUseCase: MonthUseCase
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

        repository = mock()
        timeUtil = TimeUtilImpl()

        monthUseCase = MonthUseCaseImpl(repository, timeUtil)

    }

    @Test
    fun `toBarDataSet converts studySessionList to BarDataSet`() {

        val subject = monthUseCase.toBarDataSet(studySessionList)

        assertEquals(2, subject.entryCount)
        assertEquals(1F, subject.getEntryForIndex(0).y)
        assertEquals(2F, subject.getEntryForIndex(1).y)
    }

    @Test
    fun `setLabels converts studySession list to correct label string list`() {

        val expected = listOf("2020-12-02", "2020-12-03" )
        val subject = monthUseCase.setLabels(studySessionList)

        assertEquals(expected, subject)
    }

    @Test
    fun `getMonth returns correct month `() {
        val subject = monthUseCase.getMonth(1)

        assertEquals("February", subject)
    }

    @Test
    fun `totalHours returns sum of given studySessionList`() {

        val subject = monthUseCase.totalHours(studySessionList)

        assertEquals(3F, subject.getEntryForIndex(0).y)
    }


}