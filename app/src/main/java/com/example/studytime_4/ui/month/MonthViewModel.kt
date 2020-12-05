package com.example.studytime_4.ui.month

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class MonthViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel(){

     val monthDayLabels = arrayListOf<String>("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31")
    private val nullLabels = arrayListOf<String>("No Data", "No Data", "No Data", "No Data", "No Data", "No Data", "No Data")
    private val months = arrayListOf<String>("January", "February" ,"March", "April", "May", "June", "July", "August", "September", "October", "November", "December")


    var month: String = ""

    private val currentMonth = LocalDateTime.now().monthValue
    private val currentDayOfMonth = LocalDateTime.now().dayOfMonth
    private val currentYear = LocalDateTime.now().year


    private val lastSevenStudySessions =
        repository.getLastSevenSessions(currentMonth, currentDayOfMonth, currentYear)
            .flowOn(Dispatchers.IO)

    private val monthsStudySession =
        repository.getAllSessionsWithMatchingMonth(currentMonth)
            .flowOn(Dispatchers.IO)


    private val _monthBarData = monthsStudySession.map {

        setMonthBarData(it)


    }.asLiveData(Dispatchers.Default + viewModelScope.coroutineContext)

    val monthBarData = _monthBarData


    private fun setMonthBarData(monthStudySessionList : List<StudySession>) : MonthData {

        val monthData = monthStudySessionList.mapIndexed {index, studySession  ->

            BarEntry(index.toFloat(), studySession.hours)

        }

        val labels = monthStudySessionList.map {  studySession ->
            Timber.i(studySession.date)

            studySession.date
        }
        Timber.i(labels.toString())

       return  MonthData(monthBarData = BarData(BarDataSet(monthData, months[currentMonth - 1])), labels = labels)
    }



}