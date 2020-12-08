package com.example.studytime_4.ui.week

import android.content.SharedPreferences
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.studytime_4.data.GoalData
import com.example.studytime_4.data.WeekData
import com.example.studytime_4.data.local.entities.StudySession
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
import java.util.*
import kotlin.collections.ArrayList

class WeekViewModel @ViewModelInject constructor(
    private val repository: Repository,
) : ViewModel() {

    var month: String = ""
    private val currentMonth = LocalDateTime.now().monthValue
    private val currentDayOfMonth = LocalDateTime.now().dayOfMonth
    private val currentYear = LocalDateTime.now().year

    private val lastSevenStudySessions =
        repository.getLastSevenSessions(currentMonth, currentDayOfMonth, currentYear)


    private val lastSevenSessionsHours =
        repository.getLastSevenSessionsHours(currentMonth, currentDayOfMonth)
            .map {
                setTotalWeeklyHours(it)
            }

    val goal = repository.getGoalForWeek(
        LocalDateTime.now().monthValue,
        LocalDateTime.now().year,
        LocalDateTime.now().dayOfMonth
    )

    private val _weekBarData = lastSevenStudySessions.map { list ->
       setWeekBarData(list)

    }.asLiveData(viewModelScope.coroutineContext)

    val weekBarData = _weekBarData


    val goalData = goal.combine(lastSevenSessionsHours) {goal, hours ->
        GoalData(
            limit = goal?.hours ?: 0,
            totalHours = hours
        )
    }.asLiveData(viewModelScope.coroutineContext)


    //
    private fun setWeekBarData(studySessionList: List<StudySession>) : WeekData {
        val hours = studySessionList.mapIndexed { index, studySession ->

            BarEntry( index.toFloat(), studySession.hours) //x and y hours need to be Y axis
        }
        val labels = studySessionList.mapIndexed {index, study ->
            study.date
        }

        return WeekData(
            weekBarData = BarData(BarDataSet(hours, "Hours")),
            labels = labels
        )
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
            "Weekly total hours"
        )

        return BarData(totalHoursBarDataSet)


    }

}