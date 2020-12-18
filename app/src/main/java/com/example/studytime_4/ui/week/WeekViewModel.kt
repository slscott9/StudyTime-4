package com.example.studytime_4.ui.week

import android.content.SharedPreferences
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.studytime_4.data.GoalData
import com.example.studytime_4.data.WeekData
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.local.entities.WeeklyGoal
import com.example.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class WeekViewModel @ViewModelInject constructor(
    private val repository: Repository,
) : ViewModel() {

    var month: String = ""
    private val currentMonth = LocalDateTime.now().monthValue
    private val currentDayOfMonth = LocalDateTime.now().dayOfMonth
    private val currentYear = LocalDateTime.now().year
    private val currentWeekDay = LocalDateTime.now().dayOfWeek.value
    private val weekDays = listOf<String>("S","M","T","W","T","F","S")


    private val lastSevenStudySessions =
        repository.getLastSevenSessions(((currentWeekDay + 1) * 86400).toLong()) //add one since weekDay is zero based

//    private val lastSevenStudySessions =
//        repository.getLastSevenSessions(currentMonth, currentDayOfMonth, currentYear)

    val goal = repository.getGoalForWeek(
        LocalDateTime.now().monthValue,
        LocalDateTime.now().year,
        LocalDateTime.now().dayOfMonth
    )

    private val lastSevenSessionsHours =
        repository.getLastSevenSessionsHours(currentMonth, currentDayOfMonth)
            .map { setTotalWeeklyHours(it) }

    private val _weekBarData = lastSevenStudySessions
            .map { list -> setWeekBarData(list) }
            .asLiveData(viewModelScope.coroutineContext)

    val weekBarData = _weekBarData


    val goalData =
        goal.combine(lastSevenSessionsHours) { goal, hours ->
                GoalData(
                    limit = goal?.hours ?: 0,
                    totalHours = hours
                )
            }.asLiveData(viewModelScope.coroutineContext)


    private fun setWeekBarData(studySessionList: List<StudySession>) : WeekData {

        Timber.i(studySessionList.toString())

        val weekBarData = Array<BarEntry>(7){it ->
            BarEntry(it * 1.toFloat(), null)
        }

        studySessionList.forEach {
            weekBarData[it.weekDay].y = it.hours //WEEK DAY WAS CHANGED FROM -1 WEEKDAYS ARE ZERO BASED
        }


        val hours = studySessionList.mapIndexed { index, studySession ->
            BarEntry( index.toFloat(), studySession.hours) //x and y hours need to be Y axis
        }
        val labels = studySessionList.mapIndexed {index, study ->
            study.date
        }

        val totalHours = studySessionList.map { it.hours }.sum()

        return WeekData(
            weekBarData = BarData(BarDataSet(weekBarData.asList(), "Hours")),
            labels = weekDays,
            totalHours
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



    private fun setTotalWeeklyHours(studySessions: List<Float>): BarData {

        //get totalHours for last seven study sessions
        val totalHours = studySessions.map {
            it
        }.sum()

        //Only need one entry for bar chart which is totals hours
        //x and y values were mixed up totalsHours needs to be y value
        val totalHoursBarDataSet = BarDataSet(
            arrayListOf(BarEntry( 0F, totalHours)),
            "Total weekly hours"
        )

        return BarData(totalHoursBarDataSet)


    }

}