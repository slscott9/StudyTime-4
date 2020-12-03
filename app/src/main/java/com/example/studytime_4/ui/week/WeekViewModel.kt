package com.example.studytime_4.ui.week

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.studytime_4.data.local.entities.Goal
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class WeekViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private val monthDayLabels = arrayListOf<String>(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23",
        "24",
        "25",
        "26",
        "27",
        "28",
        "29",
        "30",
        "31"
    )
    private val nullLabels = arrayListOf<String>(
        "No Data",
        "No Data",
        "No Data",
        "No Data",
        "No Data",
        "No Data",
        "No Data"
    )
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


    val datesFromSessions = ArrayList<String>()

    var month: String = ""

    private val currentMonth = LocalDateTime.now().monthValue
    private val currentDayOfMonth = LocalDateTime.now().dayOfMonth

    /*
        LiveData<List<StudySession>> only updates when view model is instantiated again

        Using flow list<StudySessions> makes this a dynamic stream. Dont need to use  list of hours live data as a trigger.

        Think about what needs to be a stream and what can be live data.

        The stream needs to be lastSevenSessions because user constantly inserts new study session into db

        the weekBarData can be live data since it is dependant on lastSevenStudySessions and is related to a state change
     */

    private val lastSevenStudySessions =
        repository.getLastSevenSessions(currentMonth, currentDayOfMonth)
            .flowOn(Dispatchers.IO)


    private val _weekBarData = lastSevenStudySessions.map { list ->
        val hours = list.mapIndexed { index, studySession ->

                BarEntry( index.toFloat(), studySession.hours) //x and y hours need to be Y axis
        }
        val labels = list.mapIndexed {index, study ->
            study.date
        }

        WeekData(
            weekBarData = BarData(BarDataSet(hours, "Hours")),
            labels = labels
        )

    }.asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)

    val weekBarData = _weekBarData


    val lastSevenSessionsHours =
        repository.getLastSevenSessionsHours(currentMonth, currentDayOfMonth)
            .map {
                setTotalWeeklyHours(it)
            }

    val goal = repository.getGoalForWeek(
        currentDate = Date().time.toInt(),
        currentDayOfMonth = LocalDateTime.now().dayOfMonth
    )

    val goalData = goal.combine(lastSevenSessionsHours) { goal, hours ->
        GoalData(
            goal.hours,
            hours
        )

    }.asLiveData()


    data class GoalData(
        val limit: Float,
        val totalHours: BarData
    )

    data class WeekData(
        val weekBarData : BarData,
        val labels : List<String>
    )

    private fun setTotalWeeklyHours(studySessions: List<Float>): BarData {

        //get totalHours for last seven study sessions
        val totalHours = studySessions.map {
            it
        }.sum()

        Timber.i("total hours are ${totalHours.toString()}")

        //Only need one entry for bar chart which is totals hours
        //x and y values were mixed up totalsHours needs to be y value
        val totalHoursBarDataSet = BarDataSet(
            arrayListOf(BarEntry( 0F, totalHours)),
            "Weekly total hours"
        )

        return BarData(totalHoursBarDataSet)


    }


//    private fun setLastSevenSessionsBarData(list: List<StudySession>): BarData {
//        var weekBarData = BarData()
//        val weekBarDataSetValues = ArrayList<BarEntry>()
//        Timber.i(list.toString())
//
//
//        if (list.isNullOrEmpty()) {
//            val barDataSet = BarDataSet(weekBarDataSetValues, "Sessions")
//            weekBarData = BarData(barDataSet)
//
//        } else {
//
//            for (session in list.indices) {
//                weekBarDataSetValues.add(
//                    BarEntry(
//                        list[session].hours,
//                        session.toFloat()
//                    )
//                )
//                datesFromSessions.add(list[session].date)
//                Timber.i(list[session].date.toString())
//            }
//            val weekBarDataSet = BarDataSet(weekBarDataSetValues, "Hours")
//            weekBarData = BarData(weekBarDataSet)
//
//        }
//
//        return weekBarData
//    }
}