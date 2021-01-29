package com.sscott.studytime_4.util

import com.sscott.studytime_4.other.util.TimeUtilImpl
import com.sscott.studytime_4.other.util.time.TimeUtil
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TimeUtilTest {

    private lateinit var timeUtil: TimeUtil

    @Before
    fun setup() {
        timeUtil = TimeUtilImpl()
    }

    @Test
    fun `formatHours returns hours when minutes parameter is greater than or equal to 60`() {

        val subject = timeUtil.formatHours(60F)

        assertEquals(1F, subject)
    }

    @Test
    fun `formatHours returns decimal minutes when minutes parameter is less than 60`() {

        val subject = timeUtil.formatHours(59F)

        assertEquals(0.59F, subject)
    }
}