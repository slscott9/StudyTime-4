package com.sscott.studytime_4.ui.usecases.timer

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TimerUseCaseImpl : TimerUseCase {

    private val _startTime = MutableLiveData<Long>()
    val startingTime : LiveData<Long> = _startTime

    private val _currentTime = _startTime
    val currentTime : LiveData<String> = _startTime.switchMap {
        liveData {
            emit(formatTime(it))
        }
    }

    private val _isRunning  = MutableLiveData<Boolean>()
    val isRunning : LiveData<Boolean> = _isRunning

    private var timer : CountDownTimer? = null

    private val _timerFinished = MutableLiveData<Boolean>()
    val timerFinished : LiveData<Boolean> = _timerFinished


    override fun startTimer() {

        timer = object : CountDownTimer(
            _currentTime.value ?: 0
            , 1000
        ) {

            override fun onFinish() {
                _timerFinished.value = true
            }

            override fun onTick(milliseconds: Long) {
                _currentTime.value = milliseconds

            }
        }

        timer?.start()
    }


    override fun stopTimer() {
        TODO("Not yet implemented")
    }

    override fun formatTime(milliseconds: Long): String {
        return  String.format( "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliseconds),
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        )
    }

    override fun cancelTimer() {
        timer?.cancel()
    }

    override fun setStartTime(milliseconds: Long) {
        _startTime.value = milliseconds
    }

    override fun cancelStartTime() {
        _startTime.value = null
    }

    override fun setCurrentTime(milliseconds: Long) {
        _currentTime.value = milliseconds
    }

    override fun cancelCurrentTime() {
        _currentTime.value = null
    }

    override fun resetTime() {
        _currentTime.value = null
        _startTime.value = null
        _isRunning.value = null
    }
}