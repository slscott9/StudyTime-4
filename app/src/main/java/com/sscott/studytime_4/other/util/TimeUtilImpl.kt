package com.sscott.studytime_4.other.util

import com.sscott.studytime_4.other.util.time.TimeUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeUtilImpl : TimeUtil {

    override fun date(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))


    override fun dayOfMonth(): Int =
        LocalDateTime.now().dayOfMonth

    override fun month(): Int =
        LocalDateTime.now().monthValue

    override fun weekDay(): Int =
        LocalDateTime.now().dayOfWeek.value

    override fun year(): Int =
        LocalDateTime.now().year

    override fun formatHours(minutes: Float): Float {
        return when {
            minutes >= 60 -> {
                minutes /60
            }
            else -> minutes / 100

        }
    }
}