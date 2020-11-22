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