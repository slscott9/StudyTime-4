package com.example.studytime_4.ui.goal

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studytime_4.data.local.entities.Goal
import com.example.studytime_4.data.repo.Repository
import kotlinx.coroutines.launch

class AddGoalViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel(){


    fun addGoal(date: Int, currentDayOfMonth: Int, hours: Float){
        viewModelScope.launch {
            repository.upsertGoal(
                Goal(
                    date = date,
                    dayOfMonth =  currentDayOfMonth,
                    hours = hours

                )
            )
        }
    }
}