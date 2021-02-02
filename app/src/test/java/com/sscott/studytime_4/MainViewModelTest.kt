package com.sscott.studytime_4

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.sscott.studytime_4.data.repo.Repository
import com.sscott.studytime_4.other.util.time.TimeUtil
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineTestRule = MainCoroutineTestRule()

    private lateinit var viewModelTest: MainViewModel

    private lateinit var repository: Repository
    private lateinit var timeUtil: TimeUtil

    @Before
    fun setup() {
        repository = mock()
        timeUtil = mock()


    }

    @Test
    fun `getMinutesStudy() return correct startTime and currentTime`()= runBlockingTest{

        viewModelTest = MainViewModel(repository, timeUtil)

        viewModelTest.setStartTime(1000)

        viewModelTest.currentTimeString.getOrAwaitValue()

        val subject = viewModelTest.getMinutesStudies()



    }
}