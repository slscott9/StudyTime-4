package com.sscott.studytime_4.data.local.entities

import androidx.room.*
import java.time.OffsetDateTime


/*
    Study session will be stored separately in order to get start and end times for each session
 */

@Entity(tableName = "study_table_4")
data class StudySession(
    @PrimaryKey
    val date : String,
    val minutes: Float,
    val weekDay: Int,
    val dayOfMonth: Int,
    val month: Int,
    val year: Int,
    val epochDate : Long,
    val startTime : String,
    val endTime : String,
    val offsetDateTime: OffsetDateTime
)

data class Durations(
    @Embedded val studySession: StudySession,
    @Relation(
        parentColumn = "date",
        entityColumn = "date"
    )
    val durationList: List<Duration>
)

@Entity(
    tableName = "duration_table",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = StudySession::class,
            parentColumns = arrayOf("date"),
            childColumns = arrayOf("date")
        )
    )
)
data class Duration(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val date: String,
    val startTime : String,
    val endTime : String,
    val minutes: Float,
    val epochDate: Long,
)
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