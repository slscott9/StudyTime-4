package com.sscott.studytime_4.other.util.time

import java.time.OffsetDateTime

interface TimeUtil {

    fun date() : String

    fun dayOfMonth() : Int

    fun month() : Int

    fun weekDay() : Int

    fun year() : Int

    fun formatHours(minutes: Float) : Float

    fun epochTimeSeconds() : Long

    fun offsetDateTime() : OffsetDateTime

}