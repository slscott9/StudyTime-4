package com.sscott.studytime_4.ui.month.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sscott.studytime_4.data.MonthData
import com.sscott.studytime_4.data.asParcelable
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.other.MONTH_SELECTED
import com.sscott.studytime_4.other.YEAR_SELECTED
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.map
import java.util.*

class MonthDetailViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //SavedStateHandle has access to navArgs

    private val calendar = Calendar.getInstance()
    private val firstDayOfMonth = firstDayOfMonth()
    private val sessionList = arrayOfNulls<com.sscott.studytime_4.data.StudySession>(42)
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
    private var _month = MutableLiveData<String>()
    val month: LiveData<String> = _month

    private val _sessionsForMonth =
        repository.monthlyStudySessions(
            savedStateHandle.get<Int>(MONTH_SELECTED)!!,
            savedStateHandle.get<Int>(YEAR_SELECTED)!!
        )

    val sessionsForMonth = _sessionsForMonth.map {
        setupSessionList(it.asParcelable())
    }.asLiveData()

    private fun setupSessionList(sessions: List<com.sscott.studytime_4.data.StudySession>): Array<com.sscott.studytime_4.data.StudySession?> {

        sessions.forEach {
            sessionList[it.dayOfMonth + WEEKDAYS + firstDayOfMonth] = it
        }

        return sessionList
    }

    private fun firstDayOfMonth(): Int {
        calendar.apply {
            set(Calendar.MONTH, savedStateHandle.get<Int>(MONTH_SELECTED)!! - 1)
            set(Calendar.YEAR, savedStateHandle.get<Int>(YEAR_SELECTED)!!)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.get(Calendar.DAY_OF_WEEK) - 1
    }


    val monthBarData = _sessionsForMonth.map {
        setMonthBarData(it)
    }.asLiveData(viewModelScope.coroutineContext)


    private val _studySession = MutableLiveData<com.sscott.studytime_4.data.StudySession>()
        val studySession: LiveData<com.sscott.studytime_4.data.StudySession> = _studySession

    val durationList = _studySession.switchMap {
        repository.studyDurations(it.date)
    }


    fun setStudySession(studySession: com.sscott.studytime_4.data.StudySession) {
        _studySession.value = studySession
    }


    private fun setMonthBarData(monthStudySessionList: List<StudySession>): MonthData {

        val monthData = monthStudySessionList.mapIndexed { index, studySession ->
            BarEntry(index.toFloat(), formatHours(studySession.minutes))
        }

        val labels = monthStudySessionList.map { studySession ->
            studySession.date
        }

        val totalHours = monthStudySessionList.map { it.minutes }.sum()

        _month.value = months[monthStudySessionList[0].month - 1]

        return MonthData(
            monthBarData = BarDataSet(monthData, "Hours"),
            labels = labels,
            totalHours = formatHours(totalHours)
        )
    }


    private fun formatHours(minutes: Float): Float {
        return when {
            minutes >= 60 -> {
                minutes / 60
            }
            else -> {
                minutes / 100
            }
        }
    }

    companion object {
        const val WEEKDAYS = 6
    }


}