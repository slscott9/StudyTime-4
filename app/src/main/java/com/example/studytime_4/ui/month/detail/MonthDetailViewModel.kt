package com.example.studytime_4.ui.month.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class MonthDetailViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel(){


    private val months = arrayListOf<String>("January", "February" ,"March", "April", "May", "June", "July", "August", "September", "October", "November", "December")


    private val currentMonth = MutableLiveData<Int>()


    private val monthsStudySession = currentMonth.switchMap {
        repository.getAllSessionsWithMatchingMonth(it).asLiveData(viewModelScope.coroutineContext)
    }


    private val _monthBarData = monthsStudySession.map {
        setMonthBarData(it)
    }

    val monthBarData = _monthBarData


    fun setMonthBarData(monthStudySessionList : List<StudySession>) : MonthData {

        val monthData = monthStudySessionList.mapIndexed {index, studySession  ->
            BarEntry(index.toFloat(), studySession.hours)
        }

        val labels = monthStudySessionList.map {  studySession ->
            studySession.date
        }

        return  MonthData(monthBarData = BarData(BarDataSet(monthData, months[monthStudySessionList[0].month - 1])), labels = labels)
    }

    fun setMonth(monthSelected: Int) {
        currentMonth.value = monthSelected
    }
}