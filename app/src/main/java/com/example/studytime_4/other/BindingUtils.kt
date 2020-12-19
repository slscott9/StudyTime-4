package com.example.studytime_4.other

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.studytime_4.data.StudySession

@BindingAdapter("setYear")
fun TextView.setYear(studySession: StudySession?){
    studySession?.let {
        text = it.year.toString()
    }
}

@BindingAdapter("setHours")
fun TextView.setHours(studySession: StudySession?){
    studySession?.let {
        text = when {
            it.hours > 1F -> {
                "${it.hours} hours"
            }
            it.hours == 1F -> {
                "${it.hours} hour"
            }
            else -> {
                "${it.hours} minutes"
            }
        }
    }
}

@BindingAdapter("setDayOfMonth")
fun TextView.setDayOfMonth(studySession: StudySession?){
    studySession?.let {
        text = "${it.dayOfMonth},"
    }
}

