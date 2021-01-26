package com.sscott.studytime_4.test

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.repo.Repository
import kotlinx.coroutines.launch

class TestViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel(){

    private val _status = MutableLiveData<Long>()

    val status: LiveData<Long> = _status


    fun insertStudySession(studySession: StudySession) {

        viewModelScope.launch {
           val id =  repository.upsertStudySession(studySession)


            _status.postValue(id)
        }
    }
}