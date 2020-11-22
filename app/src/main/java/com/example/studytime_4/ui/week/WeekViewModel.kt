package com.example.studytime_4.ui.week

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class WeekViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private val monthDayLabels = arrayListOf<String>("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31")
    private val nullLabels = arrayListOf<String>("No Data", "No Data", "No Data", "No Data", "No Data", "No Data", "No Data")
    private val months = arrayListOf<String>("January", "February" ,"March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

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


    private val _weekBarData = lastSevenStudySessions.map {
        setLastSevenSessionsBarData(it) //map is a suspending function for flow
    }.asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)

    val weekBarData = _weekBarData




    private  fun setLastSevenSessionsBarData(list: List<StudySession>): BarData {
        val weekBarDataSetValues = ArrayList<BarEntry>()
        var weekBarData = BarData()

        if (list.isNullOrEmpty()) {
            val barDataSet = BarDataSet(weekBarDataSetValues, "Sessions")
            weekBarData = BarData(nullLabels, barDataSet)

        } else {
            val datesFromSessions = ArrayList<String>()

            for (session in list.indices) {
                weekBarDataSetValues.add(
                    BarEntry(
                        list[session].hours,
                        session
                    )
                )
                datesFromSessions.add(list[session].date)
            }
            val weekBarDataSet = BarDataSet(weekBarDataSetValues, "Hours")
            weekBarData = BarData(datesFromSessions, weekBarDataSet)
        }

        return weekBarData
    }
}