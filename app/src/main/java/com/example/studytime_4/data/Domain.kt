package com.example.studytime_4.data

import com.github.mikephil.charting.data.BarData

data class GoalData(
    val limit: Int,
    val totalHours: BarData
)

data class WeekData(
    val weekBarData : BarData,
    val labels : List<String>
)