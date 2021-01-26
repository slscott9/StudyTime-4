package com.sscott.studytime_4.ui.week

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sscott.studytime_4.data.GoalData
import com.sscott.studytime_4.data.WeekData
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import com.sscott.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeekViewModel @ViewModelInject constructor(
    private val repository: Repository,
) : ViewModel() {

    var month: String = ""
    private val currentMonth = LocalDateTime.now().monthValue
    private val currentDayOfMonth = LocalDateTime.now().dayOfMonth
    /*
        1 is monday 7 is sunday
        week view displays 0 as sunday and saturday as 6
     */

    private val weekDays = listOf<String>("S","M","T","W","T","F","S")

    private val sessionsForWeek = repository.weeklyStudySessions() //weekDayMap[currentWeekDay]!! * secondsInDay

    private val lastSevenSessionsHours = repository.weeklyHours(currentMonth, currentDayOfMonth)
            .map { setTotalWeeklyHours(it) }

    val goal = repository.weeklyGoal(
        LocalDateTime.now().monthValue,
        LocalDateTime.now().year,
        LocalDateTime.now().dayOfMonth
    )

    val goalData =
        goal.combine(lastSevenSessionsHours) { goal, hours ->
                GoalData(
                    limit = goal?.hours ?: 0,
                    totalHours = hours
                )
            }.asLiveData(viewModelScope.coroutineContext)

    val weekBarData = sessionsForWeek
        .map {setWeekBarData(it) }
        .asLiveData(viewModelScope.coroutineContext)



    private fun setWeekBarData(studySessionList: List<StudySession>) : WeekData {
        val weekBarData = Array<BarEntry>(7){it ->
            BarEntry(it * 1.toFloat(), null)
        }

        studySessionList.forEach {
            weekBarData[it.weekDay].y = formatHours(it.minutes)//WEEK DAY WAS CHANGED FROM -1 WEEKDAYS ARE ZERO BASED
        }

        val totalHours = studySessionList.map { it.minutes }.sum()

        return WeekData(
            weekBarData = BarDataSet(weekBarData.asList(), "Hours"),
            labels = weekDays,
            formatHours(totalHours)
        )
    }

     fun addGoal(hours : Int){
        viewModelScope.launch {
            val id = repository.saveWeeklyGoal(
                WeeklyGoal(
                    date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    dayOfMonth =  LocalDateTime.now().dayOfMonth,
                    hours = hours,
                    month = LocalDateTime.now().monthValue,
                    weekDay = LocalDateTime.now().dayOfWeek.value,
                    year = LocalDateTime.now().year,
                )
            )
        }
    }


    private fun formatHours(minutes : Float) : Float {
        return when {
            minutes >= 60 -> {
               minutes / 60
            }
            else -> {
                minutes / 100
            }
        }
    }


    private fun setTotalWeeklyHours(studySessions: List<Float>): BarDataSet {

        //get totalHours for last seven study sessions
        val totalHours = studySessions.map { it }.sum()

        return BarDataSet(
            arrayListOf(BarEntry( 0F, formatHours(totalHours))),
            "Total weekly hours"
        )
    }

}