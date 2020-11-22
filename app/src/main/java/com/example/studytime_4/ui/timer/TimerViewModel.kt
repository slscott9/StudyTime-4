package com.example.studytime_4.ui.timer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.repo.Repository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TimerViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel(){
    private val _insertStatus = MutableLiveData<Long>()
    val insertStatus : LiveData<Long> = _insertStatus



    fun upsertStudySession(newStudySession: StudySession){
        viewModelScope.launch {
            val status = repository.upsertStudySession(newStudySession)

            _insertStatus.postValue(status)
        }
    }
}