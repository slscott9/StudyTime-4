package com.sscott.studytime_4.ui.goal

import com.sscott.studytime_4.CoroutineTestRule
import com.sscott.studytime_4.data.repo.Repository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AddGoalViewModelTest {

    private lateinit var repository: Repository
    private lateinit var subjectAddGoalViewModel : AddGoalViewModel

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    /*
        If switching dispatcher in the view model you need to inject a dispatcher interface into the view model
        The use viewMode.launch(dispatcher)

        coroutineTestRule provides a test dispatcher since we do not have access to the main dispatcher in a test

        run a test function using coroutineTestRule.runblockingTest and the test will run since a test dispatcher is now provided to the coroutine
        that has switched threads
     */



    @Before
    fun setup() {

        repository = mock()

        subjectAddGoalViewModel = AddGoalViewModel(repository)

    }

    //Whenever monthlyGoal is true repository.saveMonthlyGoal should be called
    @Test
    fun addGoal() = runBlockingTest {

        subjectAddGoalViewModel.addGoal(5 , true)

        verify(repository, times(1)).saveMonthlyGoal(any())


    }


    //Whenever viewModel.addGoal() monthlyGoal parameter is false
    //repository saveWeeklyGoals should be called once
    @Test
    fun addGoal2() = runBlockingTest {

        subjectAddGoalViewModel.addGoal(4, false)

        verify(repository, times(1)).saveWeeklyGoal(any())


    }





}