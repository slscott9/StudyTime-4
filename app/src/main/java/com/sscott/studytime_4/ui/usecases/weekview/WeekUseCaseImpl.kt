package com.sscott.studytime_4.ui.usecases.weekview

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.other.util.time.TimeUtil
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

class WeekUseCaseImpl(private val repository: Repository, private val timeUtil: TimeUtil) : WeekUseCase {

    override fun sessionsForWeek(): Flow<List<StudySession>> {
        return repository.weeklyStudySessions()
    }

    override fun weeklyGoal(): Flow<WeeklyGoal?> {
        return repository.weeklyGoal(timeUtil.month(), timeUtil.year(), timeUtil.dayOfMonth())
    }

    override fun totalHours(studySessionList: List<StudySession>): Float {
        val totalHours = studySessionList.map { it.minutes }.sum()

        return  timeUtil.formatHours(totalHours)
    }

    override fun toBarDataSet(studySessionList: List<StudySession>): BarDataSet {

        val weekBarData = Array<BarEntry>(7) { it -> BarEntry(it * 1.toFloat(), null) }

        studySessionList.forEach {
            weekBarData[it.weekDay].y = timeUtil.formatHours(it.minutes)
        }

        return BarDataSet(weekBarData.toList(), "Hours")
    }

    override suspend fun saveGoal(hours: Int) {
        repository.saveWeeklyGoal(
            WeeklyGoal(
                date = timeUtil.date(),
                dayOfMonth = timeUtil.dayOfMonth(),
                hours = hours,
                month = timeUtil.month(),
                weekDay = timeUtil.weekDay(),
                year = timeUtil.year(),
            )
        )
    }

    override suspend fun insertTestStudySession() {
        repository.upsertStudySession(
            StudySession(
                minutes = 70F,
                date = "2021-01-26",
                weekDay = 2,
                month = 1,
                dayOfMonth = 26,
                year = 2021,
                epochDate = 1611666286,
                startTime = "1 : 30 pm",
                endTime = "2: 30 pm",
                offsetDateTime = OffsetDateTime.parse("2021-01-26T16:35:37.508-06:00")
            )
        )
    }
}