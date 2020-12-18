package com.example.studytime_4.ui.sessions.sessionsdetail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.studytime_4.data.local.entities.StudySession

class SessionDetailViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel(){


    private val months = listOf<String>("January", "February", "March", "April", "May", "June", "July","August", "September", "October", "November", "December")

    private var studySession = savedStateHandle.getLiveData<com.example.studytime_4.data.StudySession>("studySession")


    val month : LiveData<String> = studySession.map {
        months[it.month - 1]
    }

    val startTime = studySession.map { it.startTime }

    val endTime = studySession.map { it.endTime }


}