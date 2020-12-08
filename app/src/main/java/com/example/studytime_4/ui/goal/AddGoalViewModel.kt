package com.example.studytime_4.ui.goal

import androidx.datastore.core.DataStore
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.WeeklyGoal
import com.example.studytime_4.data.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddGoalViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel(){



    fun addGoal( hours: Int, monthlyGoal: Boolean){
        viewModelScope.launch {

            Timber.i(Thread.currentThread().name)


            if(monthlyGoal){
                repository.saveMonthlyGoal(
                    MonthlyGoal(
                        date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        dayOfMonth =  LocalDateTime.now().dayOfMonth,
                        hours = hours,
                        month = LocalDateTime.now().monthValue,
                        weekDay = LocalDateTime.now().dayOfWeek.toString(),
                        year = LocalDateTime.now().year,
                    )
                )
            }else{
                repository.saveWeeklyGoal(
                    WeeklyGoal(
                        date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        dayOfMonth =  LocalDateTime.now().dayOfMonth,
                        hours = hours,
                        month = LocalDateTime.now().monthValue,
                        weekDay = LocalDateTime.now().dayOfWeek.toString(),
                        year = LocalDateTime.now().year,
                    )
                )
            }



        }
    }
}