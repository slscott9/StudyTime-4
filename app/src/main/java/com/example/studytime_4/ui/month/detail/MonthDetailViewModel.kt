package com.example.studytime_4.ui.month.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.repo.Repository
import com.example.studytime_4.other.MONTH_SELECTED
import com.example.studytime_4.other.YEAR_SELECTED
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class MonthDetailViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel(){

    //SavedStateHandle has access to navArgs

    private val calendar = Calendar.getInstance()

    private val months = arrayListOf<String>("January", "February" ,"March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    private val daysNums = listOf<String>("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20", "21","22","23","24","25","26","27","28", "29","30", "31")


    private val monthsStudySession =
        repository.getAllSessionsWithMatchingMonth(
            savedStateHandle.get<Int>(MONTH_SELECTED)!!,
            savedStateHandle.get<Int>(YEAR_SELECTED)!!
        )

    val monthlyHours = monthsStudySession.map {
        setMonthlyHours(it)
    }.asLiveData(viewModelScope.coroutineContext)



    val monthBarData = monthsStudySession.map {
        setMonthBarData(it)
    }.asLiveData(viewModelScope.coroutineContext)


    private fun setMonthlyHours(hours : List<StudySession>) : FloatArray {

        val monthlyHours = FloatArray(31)

        hours.forEach {
            monthlyHours[it.dayOfMonth -1] = it.hours
        }


        return monthlyHours

    }

    private fun setMonthBarData(monthStudySessionList : List<StudySession>) : MonthData {

        val monthData = monthStudySessionList.mapIndexed {index, studySession  ->
            BarEntry(index.toFloat(), studySession.hours)
        }

        val labels = monthStudySessionList.map {  studySession ->
            studySession.date
        }

        return  MonthData(monthBarData = BarData(BarDataSet(monthData, months[monthStudySessionList[0].month - 1])), labels = labels)
    }


}