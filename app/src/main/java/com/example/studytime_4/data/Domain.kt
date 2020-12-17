package com.example.studytime_4.data

import android.os.Parcel
import android.os.Parcelable
import com.example.studytime_4.data.local.entities.StudySession
import com.github.mikephil.charting.data.BarData
import kotlinx.android.parcel.Parcelize

data class GoalData(
    val limit: Int,
    val totalHours: BarData
)

data class WeekData(
    val weekBarData : BarData,
    val labels : List<String>,
    val totalHours: Float
)

data class MonthData(
    val monthBarData: BarData,
    val labels: List<String>,
    val totalHours : Float
)

fun List<StudySession>.asParcelable() : List<com.example.studytime_4.data.StudySession>{
     return map {
            com.example.studytime_4.data.StudySession(
                date = it.date,
                hours = it.hours,
                minutes = it.minutes,
                weekDay = it.weekDay,
                dayOfMonth = it.dayOfMonth,
                month = it.month,
                year = it.year
            )
     }
}

@Parcelize
data class StudySession(
    val date : String,
    val hours: Float,
    val minutes: Long,
    val weekDay: Int,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
) : Parcelable