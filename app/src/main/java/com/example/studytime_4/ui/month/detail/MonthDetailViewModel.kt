package com.example.studytime_4.ui.month.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.studytime_4.data.MonthData
import com.example.studytime_4.data.asParcelable
import com.example.studytime_4.data.local.entities.Durations
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
    private val firstDayOfMonth = firstDayOfMonth()

    private val sessionList = arrayOfNulls<com.example.studytime_4.data.StudySession>(42)

    private val months = arrayListOf<String>("January", "February" ,"March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    private var _month = MutableLiveData<String>()
    val month: LiveData<String> = _month

    private val _monthsStudySession =
        repository.monthlyStudySessions(
            savedStateHandle.get<Int>(MONTH_SELECTED)!!,
            savedStateHandle.get<Int>(YEAR_SELECTED)!!
        )

    val monthStudySessions = _monthsStudySession.map {
        setupSessionList(it.asParcelable())
    }.asLiveData()

    private fun setupSessionList(sessions : List<com.example.studytime_4.data.StudySession>) : Array<com.example.studytime_4.data.StudySession?> {

        sessions.forEach {
            sessionList[it.dayOfMonth + WEEKDAYS + firstDayOfMonth] = it
        }

        return sessionList
    }

    private fun firstDayOfMonth() : Int {
        calendar.apply {
            set(Calendar.MONTH, savedStateHandle.get<Int>(MONTH_SELECTED)!! -1)
            set(Calendar.YEAR, savedStateHandle.get<Int>(YEAR_SELECTED)!!)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.get(Calendar.DAY_OF_WEEK) - 1
    }


    val monthBarData = _monthsStudySession.map {
        setMonthBarData(it)
    }.asLiveData(viewModelScope.coroutineContext)


    private val _studySession = MutableLiveData<com.example.studytime_4.data.StudySession>()

    val studySession : LiveData<com.example.studytime_4.data.StudySession> = _studySession

    val _durationList = _studySession.switchMap {
        repository.studyDurations(it.date)
    }



    fun setStudySession(studySession: com.example.studytime_4.data.StudySession){
        _studySession.value = studySession
    }


    private fun setMonthBarData(monthStudySessionList : List<StudySession>) : MonthData {

        val monthData = monthStudySessionList.mapIndexed {index, studySession  ->
            BarEntry(index.toFloat(), studySession.hours)
        }

        val labels = monthStudySessionList.map {  studySession ->
            studySession.date
        }

        val totalHours = monthStudySessionList.map { it.hours }.sum()

        _month.value = months[monthStudySessionList[0].month -1]

        return  MonthData(
            monthBarData = BarDataSet(monthData, "Hours"),
            labels = labels,
            totalHours = totalHours)
    }

    companion object {
        const val WEEKDAYS = 6
    }


}