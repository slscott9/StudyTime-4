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


    private var timer : CountDownTimer ? = null

    private val _isRunning =  MutableLiveData<Boolean>()
    val isRunning : LiveData<Boolean> = _isRunning


    private val _currentTimeMilli = MutableLiveData<Long>()

    val currentTimeString : LiveData<String> = _currentTimeMilli.switchMap {
        liveData {
            emit(timerFormatter(it))
        }
    }

    private var starTimeHours = 0L

    private var startTime = ""
    private var endTime = ""

    private var timeAvailable = false

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished : LiveData<Boolean> = _timerFinished

    fun setTimerFinished(finished : Boolean) {
        _timerFinished.value = finished
    }

    fun setStartTime(startingTime: String){
        startTime = startingTime
    }

    fun getStartTime() : String {
        return startTime
    }

    fun setEndTime(endingTime : String){
        endTime = endingTime
    }

    fun getEndTime() : String {
        return endTime
    }

    fun isTimeAvailable() : Boolean {
        return timeAvailable
    }

    fun setIsTimeAvailable(availabe: Boolean){
        timeAvailable = availabe
    }


    fun getCurrentTimeMilli() : Long {
        return _currentTimeMilli.value ?: 0L
    }

    fun setCurrentTimeMilli(milliseconds : Long) {
        _currentTimeMilli.value = milliseconds
    }

    fun setStartTimeHours(hours : Long) {
        starTimeHours = hours
    }

    fun  getStartTimeHours() : Long {
        return starTimeHours
    }

    fun setIsRunning(running : Boolean){
        _isRunning.value = running
    }

    fun startTimer(startingTime: Long) : Boolean{

        timer = object : CountDownTimer(startingTime, 1000) {
            override fun onFinish() {
                _timerFinished.value = true
            }

            override fun onTick(milliseconds: Long) {
                Timber.i("in on tick")

                _currentTimeMilli.value = milliseconds
            }
        }

        timer?.start()
        return true
    }

    private fun timerFormatter(time_in_milli : Long) : String {

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


    /*
        Dont need to observer insertStatus since this is activity's view model.
        once insertStatus is set, when navigating backto timer fragment the live data triggers again and redirects to home fragment.
     */


    fun upsertStudySession(newStudySession: StudySession){
        viewModelScope.launch {
            val status = repository.upsertStudySession(newStudySession)

        }
    }
}