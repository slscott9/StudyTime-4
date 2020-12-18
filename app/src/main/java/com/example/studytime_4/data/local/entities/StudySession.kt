package com.example.studytime_4.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.OffsetDateTime

@Entity(tableName = "study_table_4")
data class StudySession(
    @PrimaryKey
    val date : String,
    val hours: Float,
    val minutes: Long,
    val weekDay: Int,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val epochDate : Long
) {
}



/*
    Add a goal entity

    user can add a goal for hours in week view fragment or month view fragment

    display the users hours and the goal on a bar chart

    update the chart as user adds new hours

    lool at the bookmarked stackoverflow to set a limit line in the bar chart
 */

@Entity(tableName = "weekly_goal_table")
data class WeeklyGoal(
    @PrimaryKey
    val date: String,
    val weekDay: Int,
    val dayOfMonth: Int,
    val hours: Int,
    val month: Int,
    val year: Int
)


@Entity(tableName = "monthly_goal_table")
data class MonthlyGoal(
    @PrimaryKey
    val date: String,
    val weekDay: Int,
    val dayOfMonth: Int,
    val hours: Int,
    val month: Int,
    val year: Int
)