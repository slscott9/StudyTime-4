package com.sscott.studytime_4.ui.usecases.timer

interface TimerUseCase {

    fun startTimer()

    fun stopTimer()

    fun formatTime(milliseconds : Long) : String

    fun cancelTimer()

    fun setStartTime(milliseconds: Long)

    fun cancelStartTime()

    fun setCurrentTime(milliseconds: Long)

    fun cancelCurrentTime()

    fun resetTime()
}