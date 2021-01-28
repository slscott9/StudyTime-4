package com.sscott.studytime_4.ui.month

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sscott.studytime_4.data.GoalData
import com.sscott.studytime_4.data.MonthData
import com.sscott.studytime_4.data.local.entities.MonthlyGoal
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.sscott.studytime_4.ui.usecases.monthview.MonthUseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MonthViewModel @ViewModelInject constructor(
    private val monthUseCase: MonthUseCase
) : ViewModel() {

    val sessionsForMonth = monthUseCase.sessionsForMonth()

    val monthLabels = sessionsForMonth
        .map { monthUseCase.setLabels(it) }.asLiveData(viewModelScope.coroutineContext)

    val monthlyGoal = monthUseCase.monthlyGoal().asLiveData(viewModelScope.coroutineContext)

    val totalHours = sessionsForMonth
        .map { monthUseCase.totalHours(it) }.asLiveData(viewModelScope.coroutineContext)

    val monthBarDataSet = sessionsForMonth
        .map { monthUseCase.toBarDataSet(it) }
        .asLiveData(viewModelScope.coroutineContext)

    fun saveGoal(hours : Int ){
        viewModelScope.launch {
            monthUseCase.saveGoal(hours)
        }
    }


}