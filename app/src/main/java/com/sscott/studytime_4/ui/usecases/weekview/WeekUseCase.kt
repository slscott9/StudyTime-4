package com.sscott.studytime_4.ui.usecases.weekview

import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.sscott.studytime_4.data.local.entities.StudySession
import com.sscott.studytime_4.data.local.entities.WeeklyGoal
import kotlinx.coroutines.flow.Flow

interface WeekUseCase {

    //returns week bar data set (hours can be totaled from the bar data set)
    //also just set goal bar chart from the totaled hours

    fun toBarDataSet(studySessionList : List<StudySession>) : BarDataSet

    suspend fun saveGoal(hours : Int)

    fun totalHours(studySessionList: List<StudySession>) : Float

     fun weeklyGoal() : Flow<WeeklyGoal?>

    fun sessionsForWeek() : Flow<List<StudySession>>



}