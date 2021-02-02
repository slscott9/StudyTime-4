package com.sscott.studytime_4

import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sscott.studytime_4.data.local.entities.Duration
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.other.util.time.TimeUtil
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val timeUtil: TimeUtil
) : ViewModel(){

    private val _startTimeMilli = MutableLiveData<Long>()
    val startingTime : LiveData<Long> = _startTimeMilli

    val _currentTime = MutableLiveData<Long>()

    val currentTimeString : LiveData<String> = _currentTime.switchMap {
        liveData {
            emit(formatTime(it))
        }
    }

    private var timeAvailable = false

    private val _isRunning  = MutableLiveData<Boolean>()
    val isRunning : LiveData<Boolean> = _isRunning

    private var timer : CountDownTimer? = null

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished : LiveData<Boolean> = _timerFinished

    private var startTimeHours : String? = null

    private var endTimeHours : String? = null

    fun setStartTimeHours(time : String) {
        startTimeHours = time
    }

    fun getTimeAvailable() : Boolean {
        return timeAvailable
    }

    fun setTimeAvailable(isAvailable : Boolean) {
        timeAvailable = isAvailable
    }

    fun startTimer() {
        Timber.i("viewmodel startTimer called")

        timer = object : CountDownTimer(
            _currentTime.value ?: 0
            , 1000
        ) {

            override fun onFinish() {
                _timerFinished.value = true
            }

            override fun onTick(milliseconds: Long) {
                Timber.i("onTick called milliseconds is $milliseconds")
                _currentTime.value = milliseconds

            }
        }

        timer?.start()
    }


    fun stopTimer() {
        timer?.cancel()
    }

    fun formatTime(milliseconds: Long): String {
        return  String.format( "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliseconds),
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        )
    }

    fun getCurrentTime() : Long {
        return _currentTime.value ?: 0
    }

    fun getStartTime() : Long {
        return _startTimeMilli.value ?: 0
    }

    fun setIsRunning(isRunning : Boolean){
        _isRunning.postValue(isRunning)
    }

    fun cancelTimer() {
        timer?.cancel()
    }

    fun setStartTime(milliseconds: Long) {
        _startTimeMilli.value = milliseconds
    }

    fun cancelStartTime() {
        _startTimeMilli.value = null
    }

    fun setCurrentTime(milliseconds: Long) {
        _currentTime.value = milliseconds
    }

    fun cancelCurrentTime() {
        _currentTime.value = null
    }

    fun resetTime() {
//        _currentTime.postValue(0)
//        _startTimeMilli.postValue(0)
//        _isRunning.postValue(false)
        _currentTime.value = 0
        _startTimeMilli.value = 0
        _isRunning.value = false
        timer?.cancel()
        timeAvailable = false
    }

    fun getMinutesStudies() : Float {

        Timber.i(getStartTime().toString())
        Timber.i(getCurrentTime().toString())
        val minutes = getStartTime() - getCurrentTime()
        Timber.i("minutes studies is ${_startTimeMilli.value?.minus(_currentTime.value!!)!!}")
        return TimeUnit.MILLISECONDS.toMinutes(_startTimeMilli.value?.minus(_currentTime.value!!)!!).toFloat()
    }

    fun saveSession(minutes: Float){
        viewModelScope.launch {
            repository.upsertStudySession(
                StudySession(
                    minutes = getMinutesStudies(),
                    date = timeUtil.date(),
                    weekDay = timeUtil.weekDay(),
                    month = timeUtil.month(),
                    dayOfMonth = timeUtil.dayOfMonth(),
                    year = timeUtil.year(),
                    epochDate = timeUtil.epochTimeSeconds(),
                    startTime = startTimeHours ?: "",
                    endTime = endTimeHours ?: "",
                    offsetDateTime = timeUtil.offsetDateTime()
                )
            )

            repository.insertStudyDuration(
                Duration(
                    date = timeUtil.date(),
                    startTime = startTimeHours ?: "",
                    endTime = endTimeHours ?: "",
                    epochDate = timeUtil.epochTimeSeconds(),
                    minutes = getMinutesStudies()

                )
            )
        }
    }





}