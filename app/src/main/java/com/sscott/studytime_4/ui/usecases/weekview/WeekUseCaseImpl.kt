package com.sscott.studytime_4.ui.usecases.weekview

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.other.util.time.TimeUtil
import kotlinx.coroutines.flow.Flow

class WeekUseCaseImpl(private val repository: Repository, private val timeUtil: TimeUtil) :
    WeekUseCase {

    override fun sessionsForWeek(): Flow<List<StudySession>> {
        return repository.weeklyStudySessions()
    }

    override fun weeklyGoal(): Flow<WeeklyGoal?> {
        return repository.weeklyGoal(timeUtil.month(), timeUtil.year(), timeUtil.dayOfMonth())
    }

    override fun totalHours(studySessionList: List<StudySession>): Float {
        return studySessionList.map { it.minutes }.sum().also { timeUtil.formatHours(it) }
    }

    override fun toBarDataSet(studySessionList: List<StudySession>): BarDataSet {

        val weekBarData = Array<BarEntry>(7){it -> BarEntry(it * 1.toFloat(), null)}

        studySessionList.forEach {
            weekBarData[it.weekDay].y = timeUtil.formatHours(it.minutes)
        }

        return BarDataSet(weekBarData.toList(), "Hours")
    }

    override suspend fun saveGoal(hours: Int) {
        repository.saveWeeklyGoal(
            WeeklyGoal(
                date = timeUtil.date(),
                dayOfMonth =  timeUtil.dayOfMonth(),
                hours = hours,
                month = timeUtil.month(),
                weekDay = timeUtil.weekDay(),
                year = timeUtil.year(),
            )
        )
    }




}