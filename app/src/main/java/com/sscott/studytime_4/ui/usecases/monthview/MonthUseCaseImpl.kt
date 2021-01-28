package com.sscott.studytime_4.ui.usecases.monthview

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.sscott.studytime_4.data.local.entities.MonthlyGoal
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.other.util.time.TimeUtil
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import kotlin.time.hours

class MonthUseCaseImpl(
    private val repository: Repository,
    private val timeUtil: TimeUtil
) : MonthUseCase {

    private val months = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    override fun toBarDataSet(studySessionList: List<StudySession>): BarDataSet {
        return BarDataSet(
            studySessionList.mapIndexed { index, studySession ->
                BarEntry(
                    index.toFloat(),
                    timeUtil.formatHours(studySession.minutes)
                )
            },
            getMonth(timeUtil.month())
        )
    }


    override fun setLabels(studySessionList: List<StudySession>): List<String> {
        return studySessionList.map { studySession -> studySession.date }
    }

    override fun getMonth(monthNum: Int): String {
        return months[monthNum]
    }

    override suspend fun saveGoal(hours: Int) {
        repository.saveMonthlyGoal(
            MonthlyGoal(
                date = timeUtil.date(),
                dayOfMonth = timeUtil.dayOfMonth(),
                hours = hours,
                month = timeUtil.month(),
                weekDay = timeUtil.weekDay(),
                year = timeUtil.year()
            )
        )
    }

    override fun totalHours(studySessionList: List<StudySession>): BarDataSet {
        val totalHours = studySessionList.map { it.minutes }.sum().also { timeUtil.formatHours(it) }


        Timber.i("Total hours is $totalHours")
        return BarDataSet(
            listOf(BarEntry(0F, totalHours)),
            "Total monthly hours"
        )
    }

    override fun monthlyGoal(): Flow<MonthlyGoal?> {
        return repository.monthlyGoal(timeUtil.month(), timeUtil.year())
    }

    override fun sessionsForMonth(): Flow<List<StudySession>> {
        return repository.monthlyStudySessions(timeUtil.month(), timeUtil.year())
    }
}