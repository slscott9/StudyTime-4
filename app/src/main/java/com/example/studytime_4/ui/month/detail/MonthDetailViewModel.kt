package com.example.studytime_4.ui.month.detail

import androidx.hilt.Assisted
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
import java.util.*

class MonthDetailViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val calendar = Calendar.getInstance()



    private val months = arrayListOf<String>("January", "February" ,"March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    private val daysNums = listOf<String>("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20", "21","22","23","24","25","26","27","28", "29","30", "31")

    private val currentMonth = MutableLiveData<Int>()

    fun setDays(monthSelected: Int){

        Timber.i("${savedStateHandle.get<Int>("monthSelected")}")


        calendar.set(Calendar.MONTH, savedStateHandle.get("monthSelected")!!)
        calendar.set(Calendar.YEAR, savedStateHandle.get("yearSelected")!!)
        calendar.set(Calendar.DAY_OF_MONTH, 1)


        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)

        val days = ArrayList<String>(35)
//
//        for(i in 0 until 35){
//            days[fir]
//        }
    }


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