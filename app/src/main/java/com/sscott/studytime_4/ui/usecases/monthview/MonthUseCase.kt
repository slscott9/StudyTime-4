package com.sscott.studytime_4.ui.usecases.monthview

import com.github.mikephil.charting.data.BarDataSet
import com.sscott.studytime_4.data.local.entities.MonthlyGoal
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import kotlinx.coroutines.flow.Flow

interface MonthUseCase {

    fun toBarDataSet(studySessionList : List<StudySession>) : BarDataSet

    suspend fun saveGoal(hours : Int)

    fun totalHours(studySessionList: List<StudySession>) : BarDataSet

    fun monthlyGoal() : Flow<MonthlyGoal?>

    fun sessionsForMonth() : Flow<List<StudySession>>

    fun setLabels(studySessionList: List<StudySession>) : List<String>

    fun getMonth(monthNum : Int) : String
}