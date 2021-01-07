package com.example.studytime_4.ui.month

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.studytime_4.data.GoalData
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.local.entities.WeeklyGoal
import com.example.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MonthViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private val months = arrayListOf<String>(
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
    var month: String = ""
    private val currentMonth = LocalDateTime.now().monthValue
    private val currentYear = LocalDateTime.now().year

    private val monthsStudySession =
        repository.monthlyStudySessions(currentMonth, currentYear)

    private val monthSessionHours = repository.monthlyHours(currentMonth).map {
        setTotalMonthlyHours(it)
    }

    val goal = repository.monthlyGoal(currentYear, currentMonth)

    val goalData = goal.combine(monthSessionHours) { goal, hours ->
        GoalData(
            limit = goal?.hours ?: 0,
            totalHours = hours
        )
    }.asLiveData(viewModelScope.coroutineContext)

    val monthBarData = monthsStudySession.map {
        setMonthBarData(it)
    }.asLiveData(viewModelScope.coroutineContext)


    private fun setMonthBarData(monthStudySessionList: List<StudySession>): MonthData {

        val monthData = monthStudySessionList.mapIndexed { index, studySession ->
            BarEntry(index.toFloat(), formatHours(studySession.minutes))
        }

        val labels = monthStudySessionList.map { studySession ->
            studySession.date
        }

        val totalHours = monthStudySessionList.map { it.minutes }.sum()

        return MonthData(
            monthBarData = BarDataSet(
                monthData,
                months[currentMonth - 1]
            ),
            labels = labels,
            totalHours = formatHours(totalHours)
        )
    }

    fun addGoal(hours: Int) {
        viewModelScope.launch {
            val id = repository.saveMonthlyGoal(
                MonthlyGoal(
                    date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    dayOfMonth = LocalDateTime.now().dayOfMonth,
                    hours = hours,
                    month = LocalDateTime.now().monthValue,
                    weekDay = LocalDateTime.now().dayOfWeek.value,
                    year = LocalDateTime.now().year,
                )
            )
        }
    }

    private fun setTotalMonthlyHours(studySessions: List<Float>): BarDataSet {
        val totalHours = studySessions.map {
            it
        }.sum()

        return BarDataSet(
            arrayListOf(BarEntry(0F, formatHours(totalHours))),
            "Total monthly hours"
        )
    }

    private fun formatHours(minutes: Float): Float {
        return when {
            minutes >= 60 -> {
                minutes / 60
            }
            else -> {
                minutes / 100
            }
        }
    }


}