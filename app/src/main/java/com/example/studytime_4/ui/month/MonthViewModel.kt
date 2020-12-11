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


    private val months = arrayListOf<String>("January", "February" ,"March", "April", "May", "June", "July", "August", "September", "October", "November", "December")


    var month: String = ""

    private val currentMonth = LocalDateTime.now().monthValue
    private val currentYear = LocalDateTime.now().year


    private val monthsStudySession =
        repository.getAllSessionsWithMatchingMonth(currentMonth, currentYear)


    private val _monthBarData = monthsStudySession.map {

        setMonthBarData(it)


    }.asLiveData(viewModelScope.coroutineContext)

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