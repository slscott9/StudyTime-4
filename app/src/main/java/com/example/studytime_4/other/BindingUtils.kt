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
        text = it.hours.toString()
    }
}

@BindingAdapter("setDayOfMonth")
fun TextView.setDayOfMonth(studySession: StudySession?){
    studySession?.let {
        text = it.dayOfMonth.toString()
    }
}

