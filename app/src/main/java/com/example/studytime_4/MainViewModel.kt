package com.example.studytime_4

import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.repo.Repository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel(){

    private val _currentTimeString =  MutableLiveData<String>()
    val currentTimeString : LiveData<String> = _currentTimeString

    private val _currentMili = MutableLiveData<Long>()
    val currentMili : LiveData<Long> = _currentMili

    private val _startTime =  MutableLiveData<Long>()
    val startTime : LiveData<Long> = _startTime


    private val _isRunning = MutableLiveData<Boolean>()

    private var timeAvailable = false

    val isRunning : LiveData<Boolean> = _isRunning.map {
        if(it){
            startTimer(_currentMili.value ?: _startTime.value ?: 0L)
        }else{
            cancelTimer()
        }
    }

    fun getTimeAvailable() : Boolean {
        return timeAvailable
    }

    fun setTimeAvailable(isTime : Boolean) {
        timeAvailable = isTime
    }

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished : LiveData<Boolean> = _timerFinished

    var timer : CountDownTimer? = null

    private val _insertStatus = MutableLiveData<Long>()
    val insertStatus : LiveData<Long> = _insertStatus


    fun setIsRunning(running : Boolean){
        _isRunning.value = running
    }

    private var time_in_milli = 0L

    fun getIsRunning() : Boolean {
        return _isRunning.value!!
    }

    fun startTimer(startingTime: Long) : Boolean{

        timer = object : CountDownTimer(startingTime, 1000) {
            override fun onFinish() {
                _timerFinished.value = true
            }

            override fun onTick(p0: Long) {
                Timber.i("in on tick")
                _startTime.value = p0
                _currentTimeString.value = timerFormatter(p0)
            }
        }

        timer?.start()
        return true
    }

    fun setStartTime(startingTime: Long) {
        Timber.i("setTime called startingTime = $startingTime")
        _startTime.value = startingTime
    }

    fun getCurrentTime() : Long {
        return _currentMili.value!!
    }

    fun setCurrentTimeString(time_in_milli : Long) {
        _currentTimeString.value = timerFormatter(time_in_milli)
        Timber.i(" setCurrentTimeString function time is ${timerFormatter(time_in_milli)}")
    }

    fun setCurrentMili(time_in_milli: Long){
        _currentMili.value = time_in_milli
    }


    fun timerFormatter(time_in_milli : Long) : String {

        val time = String.format( "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(time_in_milli),
            TimeUnit.MILLISECONDS.toMinutes(time_in_milli) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time_in_milli)), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(time_in_milli) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time_in_milli))
        )

        return time
    }

    fun cancelTimer() : Boolean {
        timer?.cancel()

        return false
    }

    fun upsertStudySession(newStudySession: StudySession){
        viewModelScope.launch {
            val status = repository.upsertStudySession(newStudySession)

            _insertStatus.postValue(status)
        }
    }
}