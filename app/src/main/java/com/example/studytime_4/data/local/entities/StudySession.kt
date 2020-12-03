package com.example.studytime_4.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_table_4")
data class StudySession(
    @PrimaryKey
    val date : String,
    val hours: Float,
    val minutes: Long,
    val weekDay: String,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
) {
}

/*
    Add a goal entity

    user can add a goal for hours in week view fragment or month view fragment

    display the users hours and the goal on a bar chart

    update the chart as user adds new hours

    lool at the bookmarked stackoverflow to set a limit line in the bar chart
 */

@Entity(tableName = "goal_table")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Int,
    val dayOfMonth: Int,
    val hours: Float
)