package com.example.studytime_4.other

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.studytime_4.data.StudySession
import com.example.studytime_4.data.local.entities.Duration
import java.text.DecimalFormat


val decimalFormat = DecimalFormat("#.00")

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
            it.minutes / 60 > 1F -> {
                "${decimalFormat.format(it.minutes / 60)} hours"
            }
            it.minutes / 60 == 1F -> {
                "${decimalFormat.format(it.minutes / 60)} hour"
            }
            else -> {
                "${it.minutes} minutes"
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
@BindingAdapter("setTime")
fun TextView.setTime(duration: Duration){
    duration?.let {
        text = when{
            it.minutes / 60 > 1F -> {
                "${decimalFormat.format(it.minutes / 60)} hours"
            }
            it.minutes / 60 == 1F -> {
                "${decimalFormat.format(it.minutes / 60)} hour"
            }
            else -> {
                "${it.minutes} minutes"
            }
        }
    }
}

