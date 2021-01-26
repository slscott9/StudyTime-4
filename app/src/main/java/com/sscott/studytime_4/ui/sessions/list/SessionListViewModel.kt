package com.sscott.studytime_4.ui.sessions.list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.sscott.studytime_4.data.repo.Repository

class SessionListViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel(){


    val yearsWithSessions = repository.allYearsWithSessions().asLiveData()

    private val _yearSelected = MutableLiveData<Int>()

    val monthsWithSessions = _yearSelected.switchMap {
        repository.monthsWithSessions(it).asLiveData()
    }

    fun setYearSelected(yearSelected : Int) {
        _yearSelected.value = yearSelected
    }
}