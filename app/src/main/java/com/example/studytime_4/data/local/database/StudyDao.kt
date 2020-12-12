package com.example.studytime_4.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.studytime_4.data.local.entities.MonthlyGoal
import com.example.studytime_4.data.local.entities.StudySession
import com.example.studytime_4.data.local.entities.WeeklyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {

    @Update
    suspend fun updateMonthlyGoal(goal: MonthlyGoal) //tested

    @Insert(onConflict = OnConflictStrategy.IGNORE) //ignore if the goal exists so we can update the goal
    suspend fun insertMonthlyGoal(goal: MonthlyGoal) : Long

    @Transaction
    suspend fun upsertMonthlyGoal(goal: MonthlyGoal) : Long {
        val id = insertMonthlyGoal(goal)

        if(id == -1L){
            updateMonthlyGoal(goal)
        }

        return id
    }


    @Query("select * from monthly_goal_table where(year = :curYear and month = :curMonth)")
    fun getGoalForMonth(curYear: Int, curMonth: Int) : Flow<MonthlyGoal>


    @Query("select * from weekly_goal_table where (month = :curMonth and year = :curYear and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth) ") //tested
    suspend fun checkForWeeklyGoal(curMonth: Int, curYear: Int, currentDayOfMonth: Int) : WeeklyGoal?

    @Query("select * from monthly_goal_table where (month = :curMonth and year = :curYear and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth) ")
    suspend fun checkForMonthlyGoal(curMonth: Int, curYear: Int, currentDayOfMonth: Int) : MonthlyGoal?


//    @Query("select * from goal_table where monthlyGoal = 0 and  month = :curMonth and year = :curYear and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth ")

    //Get current goal
    @Query("select * from weekly_goal_table where (month = :curMonth and year = :curYear and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth)") //tested
    fun getGoalForWeek(curMonth: Int, curYear: Int, currentDayOfMonth: Int) : Flow<WeeklyGoal?>

    //Save user goals
    @Update
    suspend fun updateWeeklyGoal(goal: WeeklyGoal) //tested

    @Insert(onConflict = OnConflictStrategy.IGNORE) //ignore if the goal exists so we can update the goal
    suspend fun insertWeeklyGoal(goal: WeeklyGoal) : Long

    @Transaction
    suspend fun upsertWeeklyGoal(goal: WeeklyGoal) : Long {
        val id = insertWeeklyGoal(goal)

        if(id == -1L){
            updateWeeklyGoal(goal)
        }

        return id
    }


    /*
        Add Goal entity with date like 2020-12-04 query database for a date in range of todays date and todays date - 7

        if there a goal and user wants to change it update the goal that was in this range

        This ensures that we only need one goal for a week instead of inserting new goals everytime user changes a goal
     */


    //Changes for transformations
    @Query("select hours from study_table_4 where month = :currentMonth and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth order by dayOfMonth asc")
    fun getLastSevenSessionsHours(currentMonth: Int, currentDayOfMonth: Int): Flow<List<Float>>


    @Query("select hours from study_table_4 where month= :monthSelected")
    fun getSessionHoursForMonth(monthSelected: Int): Flow<List<Float>>


    /*
        Should we return live data straight form the dao? The repo methods can receive parameters that we can use to query the database
     */

    @Query("select * from study_table_4 where date= :currentDate ")
    suspend fun getCurrentStudySession(currentDate: String): StudySession


    @Query("select * from study_table_4 where(month= :monthSelected and year = :yearSelected) order by dayOfMonth asc")
    fun getAllSessionsWithMatchingMonth(monthSelected: Int, yearSelected: Int): Flow<List<StudySession>>


    /*
        To get the current week's study sessions query database for current day of week
     */


    //Needs year to get the study sessions from current date
    @Query("select * from study_table_4 where (year = :curYear and month= :currentMonth and dayOfMonth between :currentDayOfMonth - 6 and :currentDayOfMonth) order by dayOfMonth asc")
    fun getLastSevenSessions(currentMonth: Int, currentDayOfMonth: Int, curYear: Int): Flow<List<StudySession>>


    @Query("select distinct year from study_table_4  order by year asc") //tested
    fun getYearsWithSessions(): Flow<List<Int>>

    @Query("select distinct month from study_table_4 where year = :yearSelected order by month asc") //tested
    fun getMonthsWithSelectedYear(yearSelected : Int) : Flow<List<Int>>


    @Query("select distinct date from study_table_4 where(month = :month and year = :year)")
    fun getDateFromSelectedMonth(month: Int, year: Int) : LiveData<String>


    /*
        The problem is when the database is empty how do you know when to call updateStudySession or insert a study session
     */

    @Transaction
    suspend fun upsertStudySession(study: StudySession ) : Long{
        val inserted = insertStudySession(study)

        if(inserted == -1L){
            updateStudySession(study.month, study.dayOfMonth, study.hours)
        }

        return inserted
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE) //tested
    suspend fun insertStudySession(study: StudySession): Long

    @Query("update study_table_4 set hours = hours + :a  where dayOfMonth= :currentDayOfMonth and month= :currentMonth")
    suspend fun updateStudySession(currentMonth: Int, currentDayOfMonth: Int, a: Float )
}