package com.sscott.studytime_4.ui.week

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sscott.studytime_4.data.GoalData
import com.sscott.studytime_4.data.WeekData
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import com.sscott.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeekViewModel @ViewModelInject constructor(
    private val weekUseCase: WeekUseCase
) : ViewModel() {

    var month: String = ""
    private val currentMonth = LocalDateTime.now().monthValue
    private val currentDayOfMonth = LocalDateTime.now().dayOfMonth
    /*
        1 is monday 7 is sunday
        week view displays 0 as sunday and saturday as 6
     */

    //These flows are not right these are one shot flows

    private val weekDays = listOf<String>("S","M","T","W","T","F","S")

    val sessionsForWeek = weekUseCase.sessionsForWeek()

    val weeklyGoal = weekUseCase.weeklyGoal().asLiveData(viewModelScope.coroutineContext)


    val totalHours = sessionsForWeek
        .map {  weekUseCase.totalHours(it) }.asLiveData(viewModelScope.coroutineContext)


    val weekBarData = sessionsForWeek
        .map { weekUseCase.toBarDataSet(it, ) }
        .asLiveData(viewModelScope.coroutineContext)



    fun saveGoal(hours : Int) {
        viewModelScope.launch {
            weekUseCase.saveGoal(hours)
        }
    }





}